#!/bin/bash
#
# check if feature dependencies can be removed as they're provided by rpm
# 
# requires that you have the devstudio rpm installed
upstreamIUs=""

IUs=$(cat ../features/com.jboss.devstudio.core.rpm.feature/feature.xml | grep import | sed -e "s/.*<import \(plugin\|feature\)=\"\([^\"]\+\)\".\+/\2/g")
for IU in ${IUs}; do
	# echo "Check $IU..."
	IUinstalled=$(find /opt/rh/ -name "${IU}*.jar" 2>/dev/null)
	for i in ${IUinstalled}; do
		if [[ ! $(echo $i | grep "/opt/rh/rh-eclipse46/root/usr/share/eclipse/droplets/devstudio/eclipse/") ]]; then 
			j=${i##*/}; j=${j%%_*}
			upstreamIUs="${upstreamIUs} ${j}"
			k=${i##*/eclipse/droplets/}; k=${k%%/eclipse/*}
			if [[ ${dropletsFolders} != *"${k}"* ]]; then dropletsFolders="${dropletsFolders} ${k}"; fi
			echo " <UPSTREAM ${i} / ${j}"
		# else
			# echo " DNSTREAM> "${i}
		fi
	done
done
echo ""

echo "Run the following commands to find which RPMs provide these droplets, then ensure they're added to devstudio.spec.template as 'Requires:' entries:"
echo ""
for droplet in ${dropletsFolders}; do
	echo "yum whatprovides /opt/rh/rh-eclipse46/root/usr/share/eclipse/droplets/${droplet} | grep rh-eclipse46 | egrep -v \"^Repo\" | sort | uniq | head -1"
done

echo ""
echo "You can then move those dependencies from ../features/com.jboss.devstudio.core.rpm.feature/feature.xml to rpmdeps.feature"