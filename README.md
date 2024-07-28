# TubeSail

TubeSail is a web application written in Java Spring for downloading videos from YouTube. YouTube does not store videos with sound on its servers, but divides them into sound and video. This site downloads pieces of video and audio, combines them and transmits them to the user. Thanks to this, the user does not wait until the video and audio from YouTube are fully downloaded to start downloading.

The project also implements registration and authorization using Spring Security. A system of roles with different rights is used for moderation and control of employees. There is an option to view the history of downloaded files.

All data is stored in the MySQL database, which is accessed via Hibernate.

https://tubesail.pp.ua
