if(LIBSODIUM_INCLUDE_DIRS AND LIBSODIUM_LIBRARIES)
    set(LIBSODIUM_FOUND TRUE)
else()
    find_package(PkgConfig QUIET)
    if(PKG_CONFIG_FOUND)
        pkg_check_modules(_SODIUM QUIET sodium)
    endif()

    if(CMAKE_SIZEOF_VOID_P EQUAL 8)
        set(_lib_suffix 64)
    else()
        set(_lib_suffix 32)
    endif()

    find_path(SODIUM_INCLUDE_DIR
            NAMES sodium.h
            HINTS
            ENV sodiumPath
            ${_SODIUM_INCLUDE_DIRS}
            /usr/include /usr/local/inculde /opt/local/include /sw/include
            )

    find_library(SODIUM_LIB
            NAMES sodium libsodium
            HINTS ${SODIUM_INCLUDE_DIR}/../lib ${SODIUM_INCLUDE_DIR}/lib${_lib_suffix} ${_SODIUM_LIBRARY_DIRS} /usr/lib /usr/local/lib /opt/local/lib /sw/lib
            )

    set(LIBSODIUM_INCLUDE_DIRS ${SODIUM_INCLUDE_DIR} CACHE PATH "sodium include dir")
    set(LIBSODIUM_LIBRARIES ${SODIUM_LIB} CACHE STRING "x265 libraries")

    find_package_handle_standard_args(Libsodium DEFAULT_MSG SODIUM_LIB SODIUM_INCLUDE_DIR)
    mark_as_advanced(SODIUM_INCLUDE_DIR SODIUM_LIB)
endif()