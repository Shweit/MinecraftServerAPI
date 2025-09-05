# MinecraftServerAPI
<img src="https://img.shields.io/github/actions/workflow/status/Shweit/MinecraftServerAPI/ci.yml" /> <img src="https://img.shields.io/github/license/Shweit/MinecraftServerAPI" />

## Overview
**MinecraftServerAPI** is a powerful and flexible plugin for Minecraft servers, providing RESTful APIs to interact with the server programmatically. This project allows server administrators to automate tasks, gather information, manage the server more efficiently, and even trigger WebHooks for various server events.

You can find a public list of all available Endpoints [here](https://msa.shweit.com/).

## Prerequisites
- **Java:** JDK 20 or higher is required to build and run the project.
- **Maven:** Make sure Maven is installed on your system. 
  You can download it [here](https://maven.apache.org/download.cgi).
- **Minecraft Server:** Make sure you have a Paper or Spigot Minecraft server running on your machine.
- **Docker:** The Test Environment is set up using Docker. 
  Make sure you have Docker installed on your system. 
  You can download it [here](https://www.docker.com/products/docker-desktop).

## Installation
### Cloning the Repository
1. Clone the repository to your local machine.
```shell
git clone git@github.com:Shweit/MinecraftServerAPI.git
cd MinecraftServerAPI
```
### Building the Project
2. Build the project using Maven.
```shell
mvn clean install
```
### Setting up the Minecraft Server
3. Copy the generated JAR file to the `plugins` directory of your Minecraft server.
```shell
cp target/MinecraftServerAPI-*.jar /path/to/your/minecraft/server/plugins
```
4. Start or restart your Minecraft server.
```shell
java -Xmx1024M -Xms1024M -jar paper-instance.jar nogui
```
5.  Once the server is running, the plugin will be loaded automatically. You can verify it by running:
```shell
/plugins
```
### Accessing the API
6. The API is accessible at `http://localhost:7000/`. You can test it by sending a GET request to the following endpoint: `GET http://localhost:7000/api/v1/ping`.

## API Usage
### Example API Requests
- **Get all Players:**
```bash
curl -X 'GET' \
  'http://localhost:7000/v1/players' \
  -H 'accept: application/json' \
  -H 'Authorization: <API_KEY>'
```

## WebHook Usage
### Configuring WebHooks
MinecraftServerAPI supports WebHooks, allowing you to trigger HTTP requests to specified URLs when certain events occur on your server (e.g., server start, server stop, plugin enable/disable).

### Setting up WebHooks
1. **Define WebHooks URLs:** In your `config.yml` located in the `plugins/MinecraftServerAPI`, specify the URLs you want to trigger for different events.
2. **Enable/Disable WebHooks:** You can enable or disable WebHooks for specific events by setting the `enabled` flag to `true` or `false` in the `config.yml` file. Or you can enable/disable the WebHooks with the following commands:
- **Enable a WebHook:**
```
/webhook enable <event>
```
- **Disable a WebHook:**
```
/webhook disable <event>
```
- **List all WebHooks:**
```
/webhook list
```

### Sending a custom WebHook
MinecraftServerAPI allows you to send custom WebHook events to the URLs specified in your config.yml file. This feature enables you to trigger specific WebHook notifications manually.

To send a custom WebHook event, use the following command in-game or via the server console:
```
/webhook send <event>
```

### More Information
For a detailed list of all available WebHooks and their default settings, please refer to the [WebHooks Documentation](webhooks.md).

## Configuration
The plugin is configured via a `config.yml` file in the `plugins/MinecraftServerAPI directory. Here, you can set the authentication key and other settings.

## Contributing
Contributions are welcome! Please read the [contributing guidelines](CONTRIBUTING.md) to get started.
