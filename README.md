# Project Avate
The project "Application for the distribution and selection of legally compliant privacy settings" 
(german: "Anwendung zur Verteilung und Auswahl rechtskonformer Datenschutzeinstellungen").
(AVARE for short) aims to strengthen the data sovereignty of individual citizens.

The [AVARE project](http://projects.aifb.kit.edu/avare/) therefore aims to help citizens protect their personal data by
to support an innovative and user-friendly software application (PRIVACY-AVARE).
The [android app](https://avare.app/) enables users to determine their data protection preferences centrally and apply them globally. 

Complete sourcecode of the project can also be found on [github](https://github.com/privacy-avare/PRIVACY-AVARE).

# Avare sync server - a microservice for synchronization of user preferences
This work focuses on the conception of a server application for data synchronization. 
The application is implemented as a microservice, a modern approach for modularized software.
For realization the java framework Spring is used.

# Starting the server
The final configuration is done with swagger UI.
Visit *http://server_IP_address:server_port/swagger-ui.html*

The default login values are:

User name: *admin*
Password: *password*

**This needs to be changed as soon as possible under: src/main/resources/application.properties**

On swagger a ‘‘profiles‘‘ database has to be generated. Just follow these four last steps and the server is ready and running.
