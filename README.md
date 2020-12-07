# Home Controller
This project is designed to control various home systems via an internet-connected interface. Its purpose is to turn internet-disconnected devices (i.e. “dumb devices”) into internet-connected devices (i.e. “smart devices”). At the time of writing this document, the current systems include the various sprinkler valves and the garage door. However, this project will enable easy additions for new systems.

## Goals

### Project Purpose:
- This project is designed to control various home systems via an internet-connected interface. Its purpose is to turn internet-disconnected devices (i.e. “dumb devices”) into internet-connected devices (i.e. “smart devices”). At the time of writing this document, the current systems include the various sprinkler valves and the garage door. However, this project will enable easy additions for new systems.

### Problems to be Solved:

- The primary problem at hand involves the pain point of manually controlling various home systems. Current systems do not offer remote control capabilities.
- Due to the limited and fixed feature set of these systems, dynamic functionality is largely impossible. If a feature is desired later, the current solution may not allow such a feature to be implemented.
- Each system has its own unique controller. This means the user is required to use multiple interfaces at different physical locations.

### How this Project Solves the Problems:
- By facilitating the controls of all home systems via an internet-connected system, the user can control everything remotely. Common tasks such as turning systems ON/OFF or monitoring the status become quick and easy.
- By using a software-defined interface, dynamic functionality is supported. This project will allow future updates to improve upon the feature set based on the user’s later requirements.
- A unified control interface means that the user must only worry about learning one interface instead of multiple.

## Sitemaps

### Main Application Sitemap:
<img src="imgs/main_app_sitemap.png"
     alt="Main Application Sitemap"
     style="float: left; margin-right: 10px;" />

## Functional Requirements

### Hardware Controller FRs:

| Requirement ID | Requirement Statement | Comments|
| ------ | ------ | ------ |
HFR001	| The controller shall keep track of the status of every connected system.	
HFR002	| The controller shall report the status of every connected system to a server.	
HFR003	| The controller shall support the addition or removal of any supported system.	
HFR004	| The controller shall support schedule-based actions for any connected systems. | This will probably require the device to fetch the current time from server on every boot-up.
HFR005 |	The controller shall apply system-specific actions based on instructions from a server.	| The initial “system-specific” actions will just include turning said system on/off.

### Application Controller FRs:

| Requirement ID | Requirement Statement | Comments|
| ------ | ------ | ------ |
AFR001	| The App shall display the status of connected systems.	
AFR002	| The App shall send actions to turn on/off connected systems.	
AFR003	| The App shall support time-based commands.	
AFR004	| The App shall support an authentication system for communitcating with the server. | This will most likely used the old "client secret token" model to support Firebase Realtime Database on Arduino.	

## Non-Functional Requirements

### Hardware Controller NFRs:
- The controller will consist of an ESP8266 for Wi-Fi connection
- The controller will also consist of an additional Arduino device for extra pin outputs
- The controller must connect/interface with a Firebase backend
- The controller must save all relevant state data to flash memory in the event of an unexpected shutdown
- Likewise, the controller must load relevant state data from flash memory if any exists

### Application Controller NFRs:
- The app must be built using Electron framework
- The app should succinctly show the state of all connected systems
- The app should have a list of systems, organized by categories of system types
- The app should allow the user to use a date/time picker to set up automatic schedule-based actions
- The app should be responsive and constantly up to date with the latest status from the backend server

## Future Features
| Feature | Description|
| ------ | ------ |
Garage Door Support | Add the ability to monitor the status of the garage door and open/close it on command.
Sprinkler Shutoff on Rain | A feature that will automatically turn off sprinklers when rain is detected. The sprinklers will not turn back on for X hours after the rain has subsided.


## Data Structures
The following is a general description of all data structures used to communicate information between the backend server, frontend application, and arduino devices. **Note:** While I will try and keep these as up-to-date as possible, the latest implementation of these datastructures can be found by viewing [Firebase Realtimedatabase](https://console.firebase.google.com/u/0/project/home-controller-286c0/database/home-controller-286c0/data).

### System
| Variable Name | Type| Description|
| ------ | ------ | ------ |
uid | String | A unique idendification value
name | String | The human-readable value that distinguishes systems. Unlike the system uid, multiple systems could theoretically share the same name. 
group | String | Systems can be grouped for better organization on front end applications. These are user-defined.
type | String | Systems are classified by types to allow front end applications to properly display supported settings/features for the specific device. (i.e. multiple sprinkler valves would all be under the "Sprinkler" system type.)
schedule | Schedule | Holds information pertaining to when the system should apply specific settings. *(see below)*
currentSettings | Settings | Holds information about the current settings applied to the arduino device. This is specifically left abstract to allow for flexible implementation of future devices that require unique settings.
enabled | Boolean | Determines whether the current system is plugged in and ready to use. Initial implementation of this feature is most likely going to be a manual switch that users can check or uncheck. If unchecked, the frontend application will appropriately handle signifying that the device isn't currently ready for remote control.

### Schedule
| Variable Name | Type| Description|
| ------ | ------ | ------ |
type | enum<TYPE> | One of: *WEEKLY* or *MONTHLY*. If the schedule is weekly, the *scheduleList* will contain 7 entries (one for each day of the week). If the schedule is monthly, the *scheduleList* will contain 31 entries.
scheduleList | List<Pair<Settings, Time>> | The list containing all the times the system should apply new settings.

### Time
| Variable Name | Type| Description|
| ------ | ------ | ------ |
start | Long | The start time (measured in milliseconds since epoch).
duration | Long | The number of milliseconds until this time period is up. After the duration expires, the device should revert to its original settings.

### Settings
| Variable Name | Type| Description|
| ------ | ------ | ------ |
options | List<Pair<String, Any>> | A flexible list of option values. Frontend applications can determine how to display this option by checking the string value. 


License
----
Copyright (C) 2020 Matthew Steinhardt


[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

