DESCRIPTION = "REDHAWK Core Framework GPP"
HOMEPAGE = "http://www.redhawksdr.org"
LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=e6a600fd5e1d9cbde2d983680233ad02"

DEPENDS = "redhawk-bulkio"
RDEPENDS_${PN} = "redhawk-bulkio"

SRC_URI = "git://github.com/RedhawkSDR/SigGen.git;branch=master;protocol=git \
file://01_Remove_x86_and_Impls.patch;patchdir=.. \
file://03_Add_Missing_Files.patch; \
file://02_Ossie_Check_fix.patch; \
file://04_Prefix_to_SDRROOT.patch; \
"

SRCREV = "eb19c78d10e579d267d3f796f39eae922fc65f0b"

PR = "r0" 

S = "${WORKDIR}/git/cpp"

# Set install location OSSIEHOME and SDRROOT
OSSIEHOME = "/usr/lib/redhawk/core"
SDRROOT = "/usr/lib/redhawk/sdr"

FILES_${PN} += "${SDRROOT}/*"
FILES_${PN}-dbg +="${SDRROOT}/dom/components/SigGen/cpp/.debug/SigGen"

# We have to inherit from pythonnative if we do stuff with the system python.
# autotools-brokensep is the sasme as autotools but our build and src locations are the same since we cannot build away from our src.
inherit autotools-brokensep pkgconfig pythonnative

EXTRA_OECONF += "--with-sdr=${SDRROOT} OSSIEHOME=${OSSIEHOME} SDRROOT=${SDRROOT} --with-boost=${STAGING_DIR_TARGET}/usr --with-boost-system=boost_system --with-boost-thread=boost_thread --with-boost-regex=boost_regex --disable-log4cxx"

# OSSIEHOME being set here will go away after I rework patch 1 from the framework. 
# Setting pymod_ossie=yes is to avoid the configure call checking for the python ossie module. This isn't ideal but it checks by running python and trying to import said module which is all cross compiled. 
# We could have it run in a native build but what does that really proove then?
CACHED_CONFIGUREVARS += "ossie_cv_ossie_home=${OSSIEHOME} ac_cv_pymod_ossie=yes"

# Needed so that when the python distutils is run it can get the system prefix which, since it's the build system python will be /.../x86_64-linux/usr and replace it with our host systems name.
do_configure_prepend() {
  export BUILD_SYS=${BUILD_SYS}
  export HOST_SYS=${HOST_SYS}
  export STAGING_INCDIR=${STAGING_INCDIR}
  export STAGING_LIBDIR=${STAGING_LIBDIR}
}

# Needed so that when the python distutils is run it can get the system prefix.
do_install_prepend() {
  export BUILD_SYS=${BUILD_SYS}
  export HOST_SYS=${HOST_SYS}
  export STAGING_INCDIR=${STAGING_INCDIR}
  export STAGING_LIBDIR=${STAGING_LIBDIR}
}

