%{?scl:%scl_package devstudio}
%{!?scl:%global pkg_name %{name}}
%{?java_common_find_provides_and_requires}
%{expand: %%global _datetime %(date -u +%Y%m%d.%H%M)}

# Prevent useless debuginfo package generation
%global debug_package %{nil}

Name:           %{?scl_prefix}devstudio
Version:        10.1
Release:        0.%{_datetime}%{?dist}
Summary:        Red Hat Developer Studio

License:        EPL
URL:            http://developers.redhat.com/products/devstudio/overview/

Source0: %{pkg_name}.tar.xz
Source1: build.sh

BuildArch: x86_64

BuildRequires: %{?scl_prefix}eclipse-pde
Requires: %{?scl_prefix}base

%description
Red Hat Developer Studio.

%prep
%{?scl:scl enable %{scl_maven} %{scl} - << "EOF"}
set -e -x
%setup -q -c
%{?scl:EOF}


%build
%{?scl:scl enable %{scl_maven} %{scl} - << "EOF"}
set -e -x
# Generate p2 repo from bundles
eclipse -nosplash -consolelog \
  -configuration /tmp/devstudio-rpm-eclipse-configuration \
  -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher \
  -metadataRepository file:$(pwd)/p2-repo \
  -artifactRepository file:$(pwd)/p2-repo \
  -source $(pwd)/%{pkg_name} \
  -publishArtifacts -compress -append
# Remove temporary eclipse config folder
rm -fr /tmp/devstudio-rpm-eclipse-configuration
%{?scl:EOF}


%install
%{?scl:scl enable %{scl_maven} %{scl} - << "EOF"}
set -e -x
# Install droplets
install -d -m755 %{buildroot}%{_datadir}/eclipse/droplets/%{pkg_name}
eclipse -nosplash -consolelog \
  -configuration /tmp/devstudio-rpm-eclipse-configuration \
  -application org.eclipse.equinox.p2.repository.repo2runnable \
  -createFragments \
  -source $(pwd)/p2-repo \
  -destination %{buildroot}%{_datadir}/eclipse/droplets/%{pkg_name}/eclipse
# Remove temporary eclipse config folder
rm -fr /tmp/devstudio-rpm-eclipse-configuration
# Remove unneeded metadata
rm %{buildroot}%{_datadir}/eclipse/droplets/%{pkg_name}/eclipse/*.jar
%{?scl:EOF}


%files
%{_datadir}/eclipse/droplets/%{pkg_name}

%changelog
* Tue Sep 20 2016 Nick Boldt <nboldt@redhat.com> 10.1.0.20160920
- Fix versioning and changelog to align with devstudio (10.x instead of 1.0.x)

* Mon Sep 19 2016 Mat Booth <mat.booth@redhat.com> - 1.0-3
- Prevent useless debuginfo package generation

* Thu Sep 15 2016 Nick Boldt <nboldt@redhat.com> - 1.0-2
- Add timestamping to package name

* Wed Aug 03 2016 Mat Booth <mat.booth@redhat.com> - 1.0-1
- Initial packaging
