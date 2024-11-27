# Set the environment variable for the .env file
env_file=./.env

set -a # Automatically export all variables
source $env_file
set +a # Stop automatically exporting

ENV_FILE=$env_file docker-compose --env-file $env_file up --build

# Pause the script execution to keep the terminal open (similar to Windows' pause)
echo "Press any key to continue..."
read -k1 -s -r
