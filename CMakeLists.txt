cmake_minimum_required(VERSION 3.5)
project(Kepler C)

set(EXECUTABLE_NAME Kepler)
set(CMAKE_C_STANDARD 11)
set(PROJECT_DIR src)
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_CURRENT_LIST_DIR}/cmake" CACHE STRING "Modules for CMake" FORCE)

INCLUDE_DIRECTORIES(src)

# Find libsodium on this system
#include(${PROJECT_DIR}/lib/libsodium/contrib/Findsodium.cmake)

# Incldude library directories
include_directories(${PROJECT_DIR}/lib/collections/)
include_directories(${PROJECT_DIR}/lib/cthreadpool/)
include_directories(${PROJECT_DIR}/lib/log/src/)
include_directories(${PROJECT_DIR}/lib/dispatch)

# Load source files for libraries
file(GLOB sqlite3 ${PROJECT_DIR}/lib/sqlite3/*.c)
file(GLOB collections ${PROJECT_DIR}/lib/collections/*.c)
file(GLOB cthreadpool ${PROJECT_DIR}/lib/cthreadpool/*.c)
file(GLOB log ${PROJECT_DIR}/lib/log/src/log.c)
file(GLOB hh_dispatch ${PROJECT_DIR}/lib/dispatch/*.c)

# Kepler classes
file(GLOB src ${PROJECT_DIR}/*.c)
file(GLOB util ${PROJECT_DIR}/util/*.c)
file(GLOB configuration ${PROJECT_DIR}/util/configuration/*.c)
file(GLOB communication ${PROJECT_DIR}/communication/*.c)
file(GLOB messages ${PROJECT_DIR}/communication/messages/*.c)
file(GLOB server ${PROJECT_DIR}/server/*.c)
file(GLOB rcon ${PROJECT_DIR}/server/rcon/*.c)
file(GLOB encoding ${PROJECT_DIR}/util/encoding/*.c)
file(GLOB database ${PROJECT_DIR}/database/*.c)
file(GLOB pathfinder ${PROJECT_DIR}/game/pathfinder/*.c)

# Query classes
file(GLOB queries ${PROJECT_DIR}/database/queries/*.c)
file(GLOB queries_items ${PROJECT_DIR}/database/queries/items/*.c)
file(GLOB queries_rooms ${PROJECT_DIR}/database/queries/rooms/*.c)

# Kepler managers
file(GLOB game ${PROJECT_DIR}/game/*.c)
file(GLOB club ${PROJECT_DIR}/game/club/*.c)
file(GLOB moderation ${PROJECT_DIR}/game/moderation/*.c)
file(GLOB players ${PROJECT_DIR}/game/player/*.c)
file(GLOB room ${PROJECT_DIR}/game/room/*.c)
file(GLOB room_public ${PROJECT_DIR}/game/room/public_rooms/*.c)
file(GLOB room_manager ${PROJECT_DIR}/game/room/manager/*.c)
file(GLOB room_mapping ${PROJECT_DIR}/game/room/mapping/*.c)
file(GLOB navigator ${PROJECT_DIR}/game/navigator/*.c)
file(GLOB items ${PROJECT_DIR}/game/items/*.c)
file(GLOB inventory ${PROJECT_DIR}/game/inventory/*.c)
file(GLOB items_definition ${PROJECT_DIR}/game/items/definition/*.c)
file(GLOB messenger ${PROJECT_DIR}/game/messenger/*.c)
file(GLOB catalogue ${PROJECT_DIR}/game/catalogue/*.c)
file(GLOB texts ${PROJECT_DIR}/game/texts/*.c)

# Tasks
file(GLOB tasks ${PROJECT_DIR}/game/room/tasks/*.c)

# Enable color in log.c library
add_definitions(-DLOG_USE_COLOR)

add_executable(${EXECUTABLE_NAME}
        ${hh_dispatch}
        ${collections}
        ${log}

        ${src}
        ${util}
        ${configuration}
        ${messages}
        ${communication}
        ${server}
        ${rcon}
        ${encoding}
        ${database}
        ${pathfinder}

        ${queries}
        ${queries_items}
        ${queries_rooms}

        ${game}
        ${moderation}
        ${club}
        ${players}
        ${navigator}
        ${items}
        ${inventory}
        ${items_definition}
        ${tasks}
        ${room}
        ${room_public}
        ${room_mapping}
        ${room_manager}
        ${messenger}
        ${catalogue}
        ${texts})

find_package (Libuv)
find_package (Libsodium)

target_link_libraries(${EXECUTABLE_NAME} uv)
target_link_libraries(${EXECUTABLE_NAME} sodium)
target_link_libraries(${EXECUTABLE_NAME} sqlite3)
