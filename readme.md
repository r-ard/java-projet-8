# Projet 8 - TourGuide App

Application de tourisme en Java SpringBoot, recommandation d'attraction touristique.

## Technologies

> Java 17  
> Spring Boot 3.X  
> JUnit 5  

## Librairies statiques du projet (services externes) :

Executer ces commandes maven :
```sh
- mvn install:install-file -Dfile="./libs/gpsUtil.jar" -DgroupId=gpsUtil -DartifactId=gpsUtil -Dversion=1.0.0 -Dpackaging=jar  

- mvn install:install-file -Dfile="./libs/RewardCentral.jar" -DgroupId=rewardCentral -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar  

- mvn install:install-file -Dfile="./libs/TripPricer.jar" -DgroupId=tripPricer -DartifactId=tripPricer -Dversion=1.0.0 -Dpackaging=jar
```

## Comment tester et build l'application en local ?

```sh
mvn clean package
```

## Pipeline CI (Git Action)

La pipeline d'intégration en continue Git Action est disponible au fichier suivant : 
> .github/workflows/master.yml

Elle est lancé à chaque push et pull_request sur les branches :
- master
- dev

Elle exécute les tâches suivantes :
- Récupérer le code source du projet
- Installer JDK 17
- Lier les dépendances externes (.jar) au projet
- Compiler et tester le projet
- Uploader le .jar du projet compilé en tant qu'artifact