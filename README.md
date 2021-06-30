# Projet indexation & recherche - FSGBD

* [Tene Ingrid](https://github.com/IngridTENE/)
* [Tcherguizova Khadijat](https://github.com/TKhadija/)
* [Mejri Mahdi](https://github.com/Mahdi-MEJRI/)

## Description 
L’exécution de la méthode main entraine l’enchainement des actions suivantes :
-	Génération automatique de la base de données : fichier à 10 000 lignes, chaque ligne contient un numéro de sécurité sociale (généré aléatoirement), un nom, un prénom, une adresse et un numéro de téléphone 
-	Création de l’index à partir du numéro de sécurité sociale et du numéro de la ligne
- Construction de l’arbre B+ à partir des numéros de sécurité sociale (les clés)
-	Sérialisation de l’arbre et de l’index : afin de pouvoir éviter de générer un nouvel arbre et un nouvel index à chaque fois qu’on veut faire des tests de recherche (une fois qu’on a fait une première exécution et qu’on a généré la base de données, l’arbre et l’index, on met le code de la méthode main en commentaire : de la ligne 69 jusqu’à la ligne 120)
-	Désérialisation de l’arbre et de l’index
-	Affichage de l’arbre 
-	Affichage séquentiel des feuilles de l’arbre 
-	Lancer 100 recherches aléatoires pour des numéros de sécurité sociale qui existent dans le fichier. Pour chaque valeur :  on fait une recherche séquentielle classique et une recherche à partir de l’index, on affiche le résultat ainsi que le temps de recherche avec chacune des deux méthodes
-	A partir des 100 recherches effectuées, on affiche le min, le max ainsi que la moyenne des temps de recherche avec chacune des deux approches 

## Résultats 
L’exécution des 100 recherches nous donne les résultats suivants :
#### Pour la recherche séquentielle classique dans le fichier 
-	Temps minimum = 84 ms
-	Temps maximum = 36 488 ms
-	Temps moyen = 15 350.29 ms
#### Pour la recherche avec l'index 
-	Temps minimum = 2 ms
-	Temps maximum = 6 ms
-	Temps moyen = 3.58 ms    

En observant le temps de recherche moyen et le temps de recherche maximum pour la recherche avec l’index, on trouve qu’il n’est pas seulement plus petit, mais il est de l’ordre de « ln (de celui pour la recherche séquentielle) / ln (2) » . Le temps de recherche séquentielle minimum est non significatif parce qu’il correspond à une ligne qui se situe au début du document. 
Ces résultats montrent que les arbres B+ et les index nous font gagner un temps énorme par rapport à une recherche séquentielle classique et confirment leur efficacité en termes de recherche de données.      

