%{?scl:%scl_package devstudio}
%{!?scl:%global pkg_name %{name}}
%{?java_common_find_provides_and_requires}

Name:           %{?scl_prefix}devstudio
Version:        1.0
Release:        1%{?dist}
Summary:        Red Hat Developer Studio

License:        EPL
URL:            http://developers.redhat.com/products/devstudio/overview/

Source0: %{pkg_name}.tar.xz
Source1: build.sh

BuildArch: noarch

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
  -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher \
  -metadataRepository file:$(pwd)/p2-repo \
  -artifactRepository file:$(pwd)/p2-repo \
  -source $(pwd)/%{pkg_name} \
  -publishArtifacts -compress -append
%{?scl:EOF}


%install
%{?scl:scl enable %{scl_maven} %{scl} - << "EOF"}
set -e -x
# Install droplets
install -d -m755 %{buildroot}%{_datadir}/eclipse/droplets/%{pkg_name}
eclipse -nosplash -consolelog \
  -application org.eclipse.equinox.p2.repository.repo2runnable \
  -createFragments \
  -source $(pwd)/p2-repo \
  -destination %{buildroot}%{_datadir}/eclipse/droplets/%{pkg_name}/eclipse
# Remove unneeded metadata
rm %{buildroot}%{_datadir}/eclipse/droplets/%{pkg_name}/eclipse/*.jar
%{?scl:EOF}


%files
%{_datadir}/eclipse/droplets/%{pkg_name}

%changelog
* Wed Aug 03 2016 Mat Booth <mat.booth@redhat.com> - 1.0-1.1
- Initial packaging.

