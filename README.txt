=== TIMBER ALERT v1.1.0 (Minecraft 26.2 Fabric) ===
Deja corrige pour Fabric 26.2 (zero fichier a modifier).

Etapes pour recuperer ton .jar via GitHub :
1. Va sur https://github.com/new (cree un compte gratuit si besoin).
2. Nomme le depot "timberalert" et clique sur "Create repository".
3. Clique sur "uploading an existing file".
4. Decompresse ce ZIP, affiche les elements masques dans Windows (pour voir le dossier ".github"), puis glisse TOUT le contenu dans GitHub et clique sur "Commit changes".
5. Clique sur l'onglet "Actions" en haut -> attends ~1 minute que la pastille devienne verte.
6. Clique sur le build vert -> descends dans "Artifacts" -> telecharge "timberalert-mc26.2-jar".
7. Decompresse le fichier recupere et mets "timberalert-1.1.0.jar" dans ton dossier mods (avec Fabric Loader 0.19.3+ et Fabric API 26.2).

Commandes en jeu :
- /timbertest  : teste le pop-up + ajoute +1 au compteur
- /timberreset : remet le compteur a 0
- /timberhud   : affiche ou cache le compteur en haut a gauche
