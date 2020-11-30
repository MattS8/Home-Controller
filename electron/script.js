var app=angular.module('myApp',['ngRoute']);
const keytar = require('keytar')

app.config(function($routeProvider){
    $routeProvider
        .when('/',{
            templateUrl:'splash.html',
            controller:'splashCtrl'
        })
        .when('/login',{
            templateUrl:'login.html',
            controller:'loginCtrl'
        })
        .when('/overview', {
            templateUrl:'overview.html',
            controller:'overviewCtrl'
        })
        .otherwise({
            redirectTo:'/'
        })
});

app.controller('overviewCtrl', function($scope, $location) {
    
});

app.controller('loginCtrl',function($scope,$location){
    
    $scope.login=function(){
        let loc = $location
        try {
            document.getElementById("emailAddress").classList.remove("is-invalid")
            document.getElementById("password").classList.remove("is-invalid")
            let triedEmail = $scope.emailAddress
            let triedPass = $scope.password

            auth.signInWithEmailAndPassword($scope.emailAddress,$scope.password)
            .catch(function(error) {
                let errorCode = error.code
                let errorMessage = error.message
                console.log("e: " + error.code)
                if (errorCode === "auth/wrong-password") {
                    console.log("Wrong password entered for " + $scope.emailAddress)
                    document.getElementById("password").classList.add("is-invalid")
                } else if (errorCode === "auth/invalid-email") {
                    document.getElementById("emailAddress").classList.add("is-invalid")
                } else if (errorCode === "auth/user-disabled") {
                    document.getElementById("password").classList.add("is-invalid")
                } else if (errorCode === "auth/user-not-found") {
                    document.getElementById("password").classList.add("is-invalid")
                } else if (errorCode === "auth/argument-error") {
                    if (errorMessage.contains("\"email\"")) {
                        document.getElementById("emailAddress").classList.add("is-invalid")
                        console.log("EMAIL BAD")
                    } else {
                        console.log("PASS BAD")
                        document.getElementById("password").classList.add("is-invalid")
                    }
                } else {
                    // UNKNOWN ERROR
                }
                //console.log(error)
            })
            .then((userCreds)=>{
                if (userCreds == null) {
                    console.log("bad user creds...")
                } else {
                    keytar.setPassword("HomeControllerService", triedEmail, triedPass)
                    $location.path("overview")
                    $scope.$apply()
                }
            }) 
        } catch (e) {  
            if (e.message.includes("\"email\"")) {
                console.log("EMAIL BAD")
                document.getElementById("emailAddress").classList.add("is-invalid")
            } else if (e.message.includes("\"password\"")) {
                document.getElementById("password").classList.add("is-invalid")
            }
            console.log(e)
        }

        
    }

    $scope.signup=function(){
        try {
            document.getElementById("emailAddress").classList.remove("is-invalid")
            document.getElementById("password").classList.remove("is-invalid")
            let triedEmail = $scope.emailAddress
            let triedPass = $scope.password

            auth.createUserWithEmailAndPassword($scope.emailAddress,$scope.password)
            .catch(error => {
                log.console(error)
            })
            .then(userCreds=>{
                if (userCreds == null) {
                    console.log("bad user creds...")
                } else {
                    console.log("HERE")
                    keytar.setPassword("HomeControllerService", triedEmail, triedPass)
                    $location.path("overview")
                    $scope.$apply()
                }
            });
        } catch (e) {  
            if (e.message.includes("\"email\"")) {
                console.log("EMAIL BAD")
                document.getElementById("emailAddress").classList.add("is-invalid")
            } else if (e.message.includes("\"password\"")) {
                document.getElementById("password").classList.add("is-invalid")
            }
            console.log(e)
        }
    }
});

app.controller('splashCtrl', function($scope, $location) {
    console.log("DEBUG ---- ATUOMATICALLY SKIPPING LOGIN")
    $location.path('overview')
    $scope.$apply()
    return
    keytar.findCredentials('HomeControllerService').then((creds) => {
        if (creds.length == 0) {
            console.log("No credentials found. Navigating immediately to the login screen...")
            $location.path('login')
            console.log("loc path: " + $location.path())
            $scope.$apply()
            //return false
        } else {
            auth.signInWithEmailAndPassword(creds[0].account, creds[0].password).then((res) => {
                if (auth.currentUser) {
                    console.log("Automatically logged into " + creds[0].account)
                    $location.path('overview')
                    $scope.$apply()
                } else {
                    $location.path('login')
                    $scope.$apply()
                }
            })
        }

    })
}) 
