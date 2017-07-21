#!/bin/bash 

# parse the Provides: data from a given RPM to see if those same artifacts are provided in another rpm
# created for JBDS-4257

usage ()
{
    echo "Usage:     $0 -r /path/to/yum_repo/rh-eclipse47-devstudio-*.el7.x86_64.rpm [-q]" 
    echo ""
    exit 1;
}

if [[ $# -lt 1 ]]; then
  usage;
fi

norm="\033[0;39m"
green="\033[1;32m"
red="\033[1;31m"
blue="\033[1;34m"

# defaults
quiet="" # or "" or "-q"

while [[ "$#" -gt 0 ]]; do
  case $1 in
    '-r') rpm="$2"; shift 1;;
    '-q') quiet="-q"; shift 0;;
  esac
  shift 1
done

log ()
{
  echo -e "$1"
}
logdebug ()
{
  if [[ ${quiet} == "" ]]; then echo -e "$1"; fi
}

now=$(date +%s);  (( now = now - 1230786000 ))

if [[ -x /usr/bin/dnf ]]; then
  YUM=dnf
else
  YUM=yum
fi

rpmfile=${rpm##*/}
log "Check what ${rpmfile} provides"
listfile=/tmp/${rpmfile}.provides.list.txt
rpm -q --provides -p ${rpm} > ${listfile}
log "Found "$(cat ${listfile} | wc -l)" provides, including devstudio and jboss entries"
log "Found "$(cat ${listfile} | egrep -v "devstudio|jboss" | wc -l)" provides, excluding devstudio and jboss entries"

pairs=$(cat ${listfile} | egrep -v "devstudio|jboss" | sed -e "s#\(.\+\) = \(.\+\)#\1=\2#")
tot=0
for pair in $pairs; do
  let tot=tot+1
done
cnt=0
for pair in ${pairs}; do
  let cnt=cnt+1
  provide=${pair%=*}
  version=${pair#*=}
  IU=${provide%)}; IU=${IU#*(}
  logdebug "  ${YUM} ${quiet} whatprovides \"${provide}\" | egrep -v \"^Repo|devstudio|test\" | grep eclipse | sort | uniq | sed -e \"s#\(.\+\) : .\+#\1#\""
  RPMs=$(${YUM} ${quiet} whatprovides "${provide}" | egrep -v "^Repo|devstudio|test" | grep eclipse | sort | uniq | sed -e "s#\(.\+\) : .\+#\1#")
  if [[ ${RPMs} ]] || [[ ${quiet} == "" ]]; then
    log "* [${cnt}/${tot}] Check ${provide} = ${version}"
  fi
  for rpm in $RPMs; do
    # check if the correct version is found
    logdebug "  * Check ${rpm} for ${IU} ..."
    result=$(rpm -ql ${rpm} 2>&1 | egrep "${IU}|${IU//.//}")
    logdebug "    rpm -ql ${rpm} 2>&1 | egrep \"${IU}\" | egrep "${version}""
    if [[ ${result} ]] && [[ $(echo ${result} | egrep "${version}") ]]; then
      log "    ? ${green}Found${norm} ${IU} in ${rpm} filesystem: ${green}${version}${norm}"
    elif [[ $(echo ${rpm} | egrep "${version}") ]]; then
      log "    + ${green}Found${norm} ${IU} in ${rpm} version: ${green}${version}${norm}"
    elif [[ ${result} ]]; then
      log "    ? ${blue}Found${norm} ${IU} in ${rpm}, but different version"
      logdebug "    - ${blue}${result}${norm}"
    else
      log "    ! ${red}Not found${norm} ${IU} in ${rpm}"
    fi
    log ""
  done
done




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
