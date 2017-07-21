#!/bin/bash -e

usage ()
{
    echo "Usage:     $0 -clean -z \"https://path/to/1.zip,https://path/to/2.zip,...\" -u \"https://path/to/site,...\" "
    echo ""
    echo "Example 1a: $0 -z \"https://devstudio.redhat.com/static/11/stable/updates/core/devstudio-11.0.0.GA-updatesite-core.zip,\\
https://devstudio.redhat.com/static/11/stable/updates/core/devstudio-11.0.0.GA-target-platform.zip,\\
https://devstudio.redhat.com/static/11/stable/updates/core/devstudio-11.0.0.GA-target-platform-central.zip,\\
https://devstudio.redhat.com/static/11/stable/updates/central/devstudio-11.0.0.GA-updatesite-central.zip\""
    echo ""
    echo "Example 1b: $0 -clean -u \"https://devstudio.redhat.com/11/stable/updates/\" -mo \"--update\""
    echo ""
    echo "Example 2: $0 -clean -u \"https://devstudio.redhat.com/11/staging/updates/\" -mo \"--update\""
    echo ""
    echo "Example 3a: $0 -clean -u \"https://devstudio.redhat.com/11/snapshots/updates/\" -mo \"--no-clean --update\""
    echo ""
    echo "Example 3b: $0 -clean -u \"https://devstudio.redhat.com/targetplatforms/jbdevstudiotarget/4.70.0.AM1-SNAPSHOT/,\\
https://devstudio.redhat.com/targetplatforms/jbtcentraltarget/4.70.0.AM1-SNAPSHOT/,\\
https://devstudio.redhat.com/11/snapshots/builds/jbosstools-discovery.central_master/latest/all/repo/,\\
https://devstudio.redhat.com/11/snapshots/builds/devstudio.product_master/latest/all/repo/\"" 
    echo ""
    echo "Example 4: $0 -clean -u \"https://devstudio.redhat.com/targetplatforms/jbdevstudiotarget/4.70.0.AM1-SNAPSHOT/,\\
https://devstudio.redhat.com/targetplatforms/jbtcentraltarget/4.70.0.AM1-SNAPSHOT/,\\
https://devstudio.redhat.com/11/snapshots/builds/jbosstools-discovery.central_master/latest/all/repo/,\\
file:///path/to/jbdevstudio-product/site/target/repository\"" 
    echo ""
    exit 1;
}

if [[ $# -lt 1 ]]; then
  usage;
fi

# defaults
quiet="" # or "" or "-q"
clean=0
source_p2_zips="" # comma-separated list passed in from commandline
source_p2_sites="" # comma-separated list passed in from commandline
JOB_NAME=rh-eclipse47-devstudio
mock_opts="" # eg., --no-clean and/or --update flags
mock_root=/var/lib/mock/ # or /opt/data/mock_root
brewrepo=http://download.devel.redhat.com/brewroot/repos/devtools-1.0-rh-eclipse47-rhel-7-build/latest/x86_64

while [[ "$#" -gt 0 ]]; do
  case $1 in
    '-clean') clean=1; shift 0;;
    '-z') source_p2_zips=",$2"; shift 1;;
    '-u') source_p2_sites=",$2"; shift 1;;
    '-q') quiet="-q"; shift 0;;
    '-j') JOB_NAME="$2"; shift 1;;
    '-mo') mock_opts="${mock_opts} $2"; shift 1;;
    '-mr') mock_root="$2"; shift 1;;
    '-b') brewroot="$2"; shift 1;;
  esac
  shift 1
done

now=$(date +%s);  (( now = now - 1230786000 ))

if [ ! $(which mock) ] ; then
  echo "Mock is not installed!"
  echo "Install with:"
  echo "$ su -c 'rpm -Uvh http://download.fedoraproject.org/pub/epel/7/x86_64/e/epel-release-7-8.noarch.rpm'"
  echo "$ su -c 'yum install mock'"
  exit 2
fi

if [ ! $(which rpmbuild) ] ; then
  echo "rpm-build is not installed!"
  echo "Install with:"
  echo "$ su -c 'yum install rpm-build redhat-rpm-config'"
  exit 2
fi

if [ ! $(getent group | grep -e '^mock' | grep -e "$USER") ] ; then
  echo "Your user is not configured to work with mock!"
  echo "Configure with:"
  echo "$ su -c '$(which usermod) -a -G mock $USER'"
  exit 2
fi

if [[ ! -f /etc/yum.repos.d/rh-eclipse47-build.repo ]]; then
  echo "Your system is not configured to resolve rh-eclipse47 packages!"
  echo "Configure with:"
  echo "$ su -c 'cp rh-eclipse47-build.repo /etc/yum.repos.d/rh-eclipse47-build.repo'"
  exit 2
fi

launcher="$(ls /opt/rh/rh-eclipse47/root/lib*/eclipse/plugins/org.eclipse.equinox.launcher_*.jar /usr/lib*/eclipse/plugins/org.eclipse.equinox.launcher_*.jar 2>/dev/null | head -1)"
#if [[ ${quiet} != "-q" ]]; then echo "[DEBUG] launcher = ${launcher}"; fi
# dnf whatprovides /usr/lib*/eclipse/plugins/org.eclipse.equinox.launcher_*.jar
# dnf whatprovides /opt/rh/rh-eclipse47/root/lib*/eclipse/plugins/org.eclipse.equinox.launcher_*.jar
if [[ ! ${launcher} ]]; then
  echo "Eclipse equinox launcher is not installed!"
  echo "Install with:"
  echo "$ su -c 'yum install eclipse-platform'"
  echo " or "
  echo "$ su -c 'yum install rh-eclipse47-eclipse-platform'"
  exit 2
fi

# Usage: p2extract ${mirror_folder} ${inputRepo1},${inputRepo2} ${featureID}
function p2extract () {
  mirror_folder="${1}"
  inputRepos="${2}"
  IUID="${3}"
  time java -jar ${launcher} \
  -application org.eclipse.equinox.p2.director \
  -clean -nosplash -consoleLog -flavor tooling \
  -profile rh-eclipse-devstudio \
  -profileProperties org.eclipse.update.install.features=true \
  -destination ${mirror_folder} \
  -bundlepool  ${mirror_folder} \
  -installIU "${IUID}" \
  -metadataRepository "${inputRepos}" \
  -artifactRepository "${inputRepos}" \
  -vmargs -Declipse.p2.MD5Check=false
}

package_name=devstudio

mirror_folder=$(pwd)/${package_name}
deps_folder=$(pwd)/${package_name}_deps
yum_repo=$(pwd)/yum_repo
mock_logs=$(pwd)/mock_logs

# clean before building
if [[ ${clean} -gt 0 ]]; then 
  rm -fr ${mirror_folder}/ ${deps_folder}/ ${mock_logs}/ ${yum_repo}/
  rm -f ${package_name}*.src.rpm ${package_name}.tar.xz 
fi
mkdir -p ${mirror_folder}

# if we're using source ZIPS instead of source sites
if [[ ${source_p2_zips} ]]; then 
  mkdir -p ${deps_folder}
  for z in ${source_p2_zips//,/ }; do
    file=${z##*/}
    if [[ ! -f ${deps_folder}/${file} ]] || [[ $(unzip -tq ${deps_folder}/${file} | egrep "cannot find") ]]; then 
      echo "[INFO] Fetch $z ..."
      time curl $z -# > ${deps_folder}/${file} &
    fi
  done
  wait
  # when done, should have 1.4G in deps_folder
  for z in ${source_p2_zips}; do
    file=${z##*/}
    source_p2_sites="${source_p2_sites},jar:file://${deps_folder}/${file}!/"
  done
fi

source_p2_sites=${source_p2_sites:1}
echo ""; echo -n "[INFO] Using p2 source sites: "; for s in ${source_p2_sites//,/, }; do echo $s; done

# error if no sites defined!
if [[ ! ${source_p2_sites} ]]; then usage; fi

featurelist=""; for f in $(cat ${package_name}.featurelist.txt | sed -e "s/^#.\+//g"); do featurelist="${featurelist},${f}.feature.group"; done; featurelist=${featurelist:1}
if [[ ${quiet} != "-q" ]]; then echo ""; echo -n "[INFO] Install these features ... "; for f in ${featurelist//,/, }; do echo $f; done; echo ""; fi

# Download features or other IUs from update sites
p2extract ${mirror_folder} ${source_p2_sites} ${featurelist}
# when done, should have 660M in mirror_folder (takes about 1 min when using zipped update sites)

# remove IUs available in the rh-eclipse47-base rpm
mirroredIUs=$(find ${mirror_folder}/{plugins,features}/ -maxdepth 1 -not -name "org.jboss.*" -a -not -name "com.jboss.*" | sort)
tot=-2 # omit features and plugins folders from the count
for iu in ${mirroredIUs}; do
  tot=$((tot+1))
done
if [[ ${quiet} != "-q" ]]; then echo "[DEBUG] Total [0] IUs in ${mirror_folder}: ${tot}"; fi

cnt=0
rpmlist="$(rpm -q --requires rh-eclipse47-base | grep -v rpmlib | sed "s#\(rh-[^=]\+\).*#\1#")" # echo $rpmlist
for iu in ${mirroredIUs}; do 
  # strip version from the IU
  iu_name=${iu##*/}; iu_name=${iu_name%.jar}
  if [[ ${iu_name} ]] && [[ ${iu_name} != "features" ]] && [[ ${iu_name} != "plugins" ]]; then
    iu_ver=$(echo ${iu_name} | sed -e "s#.\+_\([0-9]\+\)\.\([0-9]\+\)\.\([0-9]\+\).*#\1.\2.\3#")
    cnt=$((cnt+1))
    iu_name=${iu_name%%_${iu_ver}*} # trim off version suffix
    #if [[ ${quiet} != "-q" ]]; then echo "[INFO] [${cnt}/${tot}] ${iu_name} = ${iu_ver}"; fi
    # check if this IU is in rh-eclipse47-base
    match=$(rpm -q --provides ${rpmlist} | sed -rn '/rh-eclipse47-osgi\('${iu_name}'\)\ \=\ '${iu_ver}'/p')
    if [[ ${match} ]]; then
      #if [[ ${quiet} != "-q" ]]; then echo "[INFO] [1] [${cnt}/${tot}] Remove ${iu_name} ${iu_ver} == ${match}"; fi
      rm -fr ${iu}
      echo ${iu_name} >> ${package_name}.removelist.txt
    fi
    #if [[ ${quiet} != "-q" ]]; then echo ""; fi
  fi
done

mirroredIUs=$(find ${mirror_folder}/{plugins,features}/ -maxdepth 1 -not -name "org.jboss.*" -a -not -name "com.jboss.*" | sort)
tot=-2 # omit features and plugins folders from the count
for iu in ${mirroredIUs}; do
  tot=$((tot+1))
done
if [[ ${quiet} != "-q" ]]; then echo "[DEBUG] Total [1] IUs in ${mirror_folder}: ${tot}"; fi

# remove IUs available in other rpms; depends on rh-eclipse47-devstudio already being installed; otherwise skip this step
# Generate list of features & plugins provided by rh-eclipse47-devstudio
productPath=/opt/rh/rh-eclipse47/root/usr/share/eclipse/droplets/${package_name}/eclipse
if [[ -d ${productPath} ]]; then 
  for iu in ${productPath}/{features,plugins}/*; do
    productIUs="$productIUs $(basename $iu | rev | cut -d_ -f1 --complement | rev)"
  done

  # Check for duplicates provided by other packages
  archful=/opt/rh/rh-eclipse47/root/usr/lib64/eclipse
  noarch=/opt/rh/rh-eclipse47/root/usr/share/eclipse
  for IUtype in features plugins; do
    for iu in $archful/${IUtype}/* {${archful},${noarch}}/droplets/*/eclipse/${IUtype}/*; do
      if [ -e "$iu" ] ; then
        # iu_name=$(basename $iu | rev | cut -d_ -f1 --complement | rev)
        iu_name=${iu##*/}; iu_name=${iu_name%.jar}
        iu_ver=$(echo ${iu_name} | sed -e "s#.\+_\([0-9]\+\)\.\([0-9]\+\)\.\([0-9]\+\).*#\1.\2.\3#")
        iu_name=${iu_name%%_${iu_ver}*} # trim off version suffix
        for productIU in $productIUs ; do
          if [ "$iu_name" == "$productIU" ] ; then
            pkg=$(rpm -qf $iu | rev | cut -d- -f1,2 --complement | rev)
            if [ "$pkg" != "rh-eclipse47-${package_name}" ] ; then
              #if [[ ${quiet} != "-q" ]]; then echo "[INFO] ${IUtype%s} ${iu_name} is provided by $pkg"; fi
              #if [[ ${quiet} != "-q" ]]; then echo "[DEBUG] match ==> find ${mirror_folder}/${IUtype} -maxdepth 1 -name \"${iu_name}_*\""; fi
              match="$(find ${mirror_folder}/${IUtype} -maxdepth 1 -name "${iu_name}_*")"
              if [[ $match ]]; then 
                for m in ${match}; do
                  #if [[ ${quiet} != "-q" ]]; then echo "[DEBUG] Check if "${m/${iu_name}_${iu_ver}/}" != "${m}", for ${iu_name}_${iu_ver}"; fi
                  if [[ "${m/${iu_name}_${iu_ver}/}" != "${m}" ]]; then
                    #if [[ ${quiet} != "-q" ]]; then echo "[INFO] [2] Remove ${iu_name}_${iu_ver} :: $m"; fi
                    rm -fr ${m}
                    echo ${iu_name} >> ${package_name}.removelist.txt
                  fi
                done
              fi
            fi
          fi
        done
      fi
    done
  done
fi

# manual IU removals to avoid singleton problems on eclipse startup
blacklist=""; for iu in $(cat ${package_name}.blacklist.txt ${package_name}.removelist.txt | sed -e "s/^#.\+//g"); do blacklist="${blacklist} ${iu}"; done
for iu in ${blacklist}; do
  # if [[ ${quiet} != "-q" ]]; then echo "Remove ${iu}_*"; fi
  rm -fr ${mirror_folder}/*/${iu}_*
  echo ${iu} >> ${package_name}.removelist.txt
done

mirroredIUs=$(find ${mirror_folder}/{plugins,features}/ -maxdepth 1 -not -name "org.jboss.*" -a -not -name "com.jboss.*" | sort)
tot=-2 # omit features and plugins folders from the count
for iu in ${mirroredIUs}; do
  tot=$((tot+1))
done
if [[ ${quiet} != "-q" ]]; then echo "[DEBUG] Total [2] IUs in ${mirror_folder}: ${tot}"; fi

# clean up the removelist file
cat ${package_name}.removelist.txt | sort | uniq > ${package_name}.removelist.txt.2
mv -f ${package_name}.removelist.txt.2 ${package_name}.removelist.txt
if [[ ${quiet} != "-q" ]]; then echo "[DEBUG] Total [3] IUs in ${package_name}.removelist.txt: "$(cat ${package_name}.removelist.txt | wc -l); fi

echo ""; echo "[INFO] Build devstudio.tar.xz ..."
time tar caf ${package_name}.tar.xz ${package_name}/
# when done (~5mins), 450M devstudio-1.0-1.fc24.src.rpm and devstudio.tar.xz created

# pass dynamic values into the .spec file
BUILD_VERSION=$(cat ../pom.xml | grep version | head -2 | tail -1 | sed -e "s#.\+<version>\([0-9.]\+\)-SNAPSHOT<\/version>.*#\1#"); echo BUILD_VERSION = ${BUILD_VERSION}
RPM_VERSION=${BUILD_VERSION%.*}; echo RPM_VERSION = ${RPM_VERSION} # 10.2
RPM_BUILD_VERSION=${BUILD_VERSION##*.}.$(date -u +%Y%m%d.%H%M); echo RPM_BUILD_VERSION = ${RPM_BUILD_VERSION} # 0.yyyymmdd.HHMM
cat ${package_name}.spec.template | sed -e "s#RPM_VERSION#${RPM_VERSION}#g" -e "s#RPM_BUILD_VERSION#${RPM_BUILD_VERSION}#g" > ${package_name}.spec
echo ""; echo "[INFO] Build rpm using ${package_name}.spec ..."

echo "[INFO] ## BEGIN RPMBUILD ##"
time rpmbuild \
  --define "_sourcedir $(pwd)" \
  --define "_srcrpmdir $(pwd)" \
  --define "_builddir $(pwd)" \
  --define "_rpmdir $(pwd)" \
  --define "_specdir $(pwd)" \
  -bs ${package_name}.spec
echo "[INFO] ## END RPMBUILD ##"

# Run the build in a mock chroot containing RHEL 7 and everything needed
# to build SCL packages
cat <<EOF >${JOB_NAME}.cfg
config_opts['chroothome'] = '/builddir'
config_opts['use_host_resolv'] = False
config_opts['basedir'] = '${mock_root}'
config_opts['rpmbuild_timeout'] = 86400
config_opts['yum.conf'] = '[main]\ncachedir=/var/cache/yum\ndebuglevel=2\n\nlogfile=/var/log/yum.log\nreposdir=/dev/null\nretries=20\nobsoletes=1\ngpgcheck=0\nassumeyes=1\nkeepcache=1\ninstall_weak_deps=0\nstrict=0\n\n[build]\nname=build\nbaseurl=${brewrepo}\nenabled=1\ngpgcheck=0'
config_opts['chroot_setup_cmd'] = 'groupinstall build'
config_opts['target_arch'] = 'x86_64'
config_opts['root'] = '${JOB_NAME}'
config_opts['plugin_conf']['root_cache_enable'] = True
config_opts['plugin_conf']['yum_cache_enable'] = True
config_opts['plugin_conf']['ccache_enable'] = False
config_opts['macros']['%_host'] = 'x86_64-koji-linux-gnu'
config_opts['macros']['%_host_cpu'] = 'x86_64'
config_opts['macros']['%vendor'] = 'Koji'
config_opts['macros']['%distribution'] = 'el7'
config_opts['macros']['%_topdir'] = '/builddir/build'
config_opts['macros']['%_rpmfilename'] = '%%{NAME}-%%{VERSION}-%%{RELEASE}.%%{ARCH}.rpm'
config_opts['macros']['%packager'] = 'Koji'
EOF

# purge old mock logs
if [[ -d ${mock_root}/${JOB_NAME}/result ]] && [[ $(ls ${mock_root}/${JOB_NAME}/result/*.log) ]]; then
  rm -f ${mock_root}/${JOB_NAME}/result/*.log
fi

echo "[INFO] ## BEGIN MOCK ##"
time /usr/bin/mock -r $(pwd)/${JOB_NAME}.cfg ${mock_opts} --rebuild ${package_name}*.src.rpm
echo "[INFO] ## END MOCK ##"

# collect new mock logs, if any
if [[ -d ${mock_root}/${JOB_NAME}/result ]] && [[ $(ls ${mock_root}/${JOB_NAME}/result/*.log) ]]; then
  mkdir -p ${mock_logs}/
  cp ${mock_root}/${JOB_NAME}/result/*.log ${mock_logs}/
fi

# Generate yum repository
rm -rf ${yum_repo}
mkdir ${yum_repo}
mv ${mock_root}/${JOB_NAME}/result/*.rpm ${yum_repo}
# keep src.rpm, do not delete # rm -f ${yum_repo}/*.src.rpm
time createrepo_c ${yum_repo}
echo "[INFO] Yum repository generated in: ${yum_repo}"

# create sha256sum
rpmfiles=$(find ${yum_repo} -maxdepth 1 -type f -name "*.rpm")
for z in ${rpmfiles}; do for shasum in $(sha256sum ${z}); do if [[ $shasum != ${z} ]]; then echo $shasum > ${z}.sha256; fi; done; done

# cleanup temp artifacts
rm -f ${package_name}*.src.rpm ${package_name}.tar.xz ${JOB_NAME}.cfg

# copy README into yum_repo folder, replacing RPM_VERSION with actual version
RPM_VERSION=$(find ${yum_repo} -maxdepth 1 -type f -name "*.rpm" | grep -v ".src.rpm" | sort | head -1 | sed -e "s#.\+devstudio-\(.\+\).rpm#\1#")
cat README.html | sed -e "s#RPM_VERSION#${RPM_VERSION}#g" > ${yum_repo}/README.html

sec=$(date +%s); (( sec = sec - 1230786000 ))
(( elapsed = sec - now ))

if [[ elapsed -gt 60 ]]; then 
  (( mins = elapsed / 60 )) 
  (( sec = mins * 60 ))
  (( sec = elapsed - sec ))
  echo "[INFO] Total time: ${mins}:${sec} min"
else
  echo "[INFO] Total time: ${elapsed} sec"
fi
