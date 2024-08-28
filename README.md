# MinecraftServerAPI

## Overview
MinecraftServerAPI is a powerful and flexible plugin for Minecraft servers, 
providing RESTful APIs to interact with the server programmatically. 
This project allows server administrators to automate tasks, gather 
information, and manage the server more efficiently.

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
cp target/MinecraftServerAPI-1.0.jar /path/to/your/minecraft/server/plugins
```
4. Start or restart your Minecraft server.
```shell
java -Xmx1024M -Xms1024M -jar paper-1.21.jar nogui
```
5.  Once the server is running, the plugin will be loaded automatically. You can verify it by running:
```shell
/plugins
```
### Accessing the API
6. The API is accessible at `http://localhost:7000/`. You can test it by sending a GET request to the following endpoint: `GET http://localhost:7000/api/v1/ping`.

## Usage
### Example API Requests
- **Get all Players:**
```bash
curl -X 'GET' \
  'http://localhost:7000/v1/players' \
  -H 'accept: application/json' \
  -H 'Authorization: <API_KEY>'
```
### Configuration
The plugin is configured via a `config.yml` file in the `plugins/MinecraftServerAPI directory. Here, you can set the authentication key and other settings.

## Contributing
Contributions are welcome! Please read the [contributing guidelines](CONTRIBUTING.md) to get started.