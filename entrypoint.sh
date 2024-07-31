#!/bin/bash
set -e

INI_FILE="/kepler/server.ini"

# Function to check for the existence of server.ini
check_for_ini_file() {
  if [ -f $INI_FILE ]; then
    return 0
  else
    return 1
  fi
}

if ! check_for_ini_file; then
    echo "[+] server.ini file not detected, running Kepler to create it..."
    ./run.sh &> /dev/null
    echo "[+] Cleaning up logs..."
    rm -f server.log error.log
fi

echo "[+] server.ini file detected !"

# Function to update the .ini file
update_ini_file() {
    local key=$1
    local value=$2
    local ini_file=$3
    local default_value=$4

    if [ -n "$value" ]; then
        if grep -q "^$key=" "$ini_file"; then
            sed -i "s|^$key=.*|$key=$value|" "$ini_file"
        else
            echo "$key=$value" >> "$ini_file"
        fi
    else
        if grep -q "^$key=" "$ini_file"; then
            sed -i "s|^$key=.*|$key=$default_value|" "$ini_file"
        else
            echo "$key=$default_value" >> "$ini_file"
        fi
    fi

    
}

echo "[+] Configuring server.ini with environment variables..."

# Server
update_ini_file "server.bind" "$SERVER_BIND" "$INI_FILE" "127.0.0.1"
update_ini_file "server.port" "$SERVER_PORT" "$INI_FILE" "12321"

# Rcon
update_ini_file "rcon.bind" "$RCON_BIND" "$INI_FILE" "127.0.0.1"
update_ini_file "rcon.port" "$RCON_PORT" "$INI_FILE" "12309"

# Mus
update_ini_file "mus.bind" "$MUS_BIND" "$INI_FILE" "127.0.0.1"
update_ini_file "mus.port" "$MUS_PORT" "$INI_FILE" "12322"

# Database
update_ini_file "mysql.hostname" "$MYSQL_HOSTNAME" "$INI_FILE" "127.0.0.1"
update_ini_file "mysql.port" "$MYSQL_PORT" "$INI_FILE" "3306"
update_ini_file "mysql.username" "$MYSQL_USER" "$INI_FILE" "kepler"
update_ini_file "mysql.password" "$MYSQL_PASSWORD" "$INI_FILE" "verysecret"
update_ini_file "mysql.database" "$MYSQL_DATABASE" "$INI_FILE" "kepler"

# Logging
update_ini_file "log.received.packets" "$LOG_RECEIVED_PACKETS" "$INI_FILE" "false"
update_ini_file "log.sent.packets" "$LOG_SENT_PACKETS" "$INI_FILE" "false"

# Console
update_ini_file "debug" "$DEBUG" "$INI_FILE" "false"

echo "[+] Running Kepler..."

# Execute the command passed to the entrypoint
exec "$@"