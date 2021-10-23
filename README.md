# DogFinder
A simple mvvm-clean example app for showing various dog images 


## Architecture : 
The app uses mvvm clean architecture for scalability and easy testing
- `api` provides info regarding how the data should be requested/what will be the data that we will receive from the server
- `repo` provides info regarding how the response of server should be parsed before sending to ui layers
- `usecase` provides a mechanism to make a call via repo. since android and other user environments require a "main" thread that is free for various tasks, this layer provides a mechanism to deal with long running api calls
- `viewmodel` provides lifecycle friendly platform for making request and performing various business logics
- `activity and other ui components` provides mechanism to display data and interact with user

![](https://miro.medium.com/max/896/1*SjczBI6N688JKSiBiYoTcA.png)
