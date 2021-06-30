package fr.miage.fsgbd;


import com.sun.source.tree.Tree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

	void createBase(String nomBd) {

		// Cr�ation du fichier
		try {
			File myObj = new File(nomBd);
			if (myObj.createNewFile()) {
				System.out.println("Le fichier a ete cree : " + myObj.getName());
			} else {
				System.out.println("Le fichier existe deja.");
			}
		} catch (IOException e) {
			System.out.println("Erreur.");
			e.printStackTrace();
		}


		// G�neration des lignes de la base
		long numSecu;
		try {
			FileWriter myWriter = new FileWriter(nomBd);
			myWriter.write( "    N� S�curit� sociale     Nom        Pr�nom        Adresse        Telephone \n");
			int i;
			for (i=1; i<=10000; i++ ) {
				numSecu = (long) (100000000000000L + Math.random() * 99999999999999l);
				myWriter.write(i + ".    " + numSecu + "         nom " + i + "      prenom " + i
						+ "      adresse " + i + "      telephone " + i + "\n");
			}
			myWriter.close();
			System.out.println("Succés de l'écriture sur le fichier.");
		} catch (IOException e) {
			System.out.println("Erreur.");
			e.printStackTrace();
		}
	}


	String rechSeqFich(long val, String nomBd) throws IOException {
		String ligne;
		long nSecu;
		for (int i=1; i<= 10000; i++) {
			ligne = Files.readAllLines(Paths.get(nomBd)).get(i);
			String s[] = ligne.split("\\s+");
			nSecu = Long.valueOf(s[1]);
			if (nSecu == val) return ligne;
		}
		return "La numero de securite sociale recherche n'existe pas !";
	}




    public static void main(String args[]) throws IOException {

		Main main = new Main();

		/*
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //On cr�e une nouvelle instance de notre JDialog
                GUI fenetre = new GUI();
                fenetre.setVisible(true);
            }
        });
        */


		Map index1 = new TreeMap();
		Map index2 = new TreeMap();

		// On cree le fichier BD
		String nomBd = "BD.txt";
		main.createBase(nomBd);

		// On lit le fichier et on construit l'index � partir du num de s�cu et du num de la ligne
		System.out.println("**********************************************************************************");
		System.out.println("Génération de l'index à partir du fichier ...");
		String ligne;
		long nSecu;
		for (int i=1; i<= 10000; i++) {
			ligne = Files.readAllLines(Paths.get(nomBd)).get(i);
			String s[] = ligne.split("\\s+");
			nSecu = Long.valueOf(s[1]);
			index1.put(i, nSecu);
			index2.put(nSecu, i);
			System.out.println(nSecu + " ---> " + i);
		}
		// On g�nere un arbre B+ � partir de l'index (num de s�cu qui pointe vers le num�ro de la ligne dans le fichier)
		System.out.println("**********************************************************************************");
		System.out.println("Construction de l'arbre");
		TestLong testLong = new TestLong();
		fr.miage.fsgbd.BTreePlus<Long> bLong = new fr.miage.fsgbd.BTreePlus<Long>(2,  testLong);

		for (int i=1; i<= 10000; i++) {
			System.out.println("Num de secu " + index1.get(i) + " --> ligne "  + i + " dans le fichier ");
			bLong.addValeur((Long) index1.get(i));
			bLong.afficheArbre();
		}

		// On sauve l'arbre et les indexs pour faire les tests sur le temps de recherche
		BSerializer<Long> save = new BSerializer<Long>(bLong, "arbre.abr");

		IndexSerializer saveIndex = new IndexSerializer(index2, "index.abr");

		//Ce tableau sera utile pour les tests, il permet de générer des valeurs aléatoires à partir du fichier
		IndexSerializer saveIndex2 = new IndexSerializer(index1, "index2.abr");


		// Si on met tout le code de la methode main qui precede cette phrase (jusqu'à la ligne 69)
		// en commentaire, on peut faire des traitements sur l'arbre precedent (serialise) sans generer un
		// nouveau fichier et un nouvel arbre et un nouvel index

		System.out.println("\n**********************************************************************************");
		System.out.println("**********************************************************************************");
		System.out.println("\nDéserialisation de l'arbre et de l'index ...");

		// On charge l'arbre et les indexs
		BDeserializer<Long> load = new BDeserializer<Long>();
		fr.miage.fsgbd.BTreePlus<Long> bLongChargee = load.getArbre("arbre.abr");

		IndexDeserializer loadIndex = new IndexDeserializer();
		TreeMap indexChargee = (TreeMap) loadIndex.getIndex("index.abr");

		IndexDeserializer loadIndex2 = new IndexDeserializer();
		TreeMap indexChargee2 = (TreeMap) loadIndex.getIndex("index2.abr");

		System.out.println("\n**********************************************************************************");
		System.out.println("\nAffichage de l'arbre\n");
		bLongChargee.afficheArbre();

		// Affichage séquenciel des feuilles de l'arbre (une feuille par ligne)
		System.out.println("\n**********************************************************************************");
		System.out.println("\nAffichage séquenciel des feuilles (une feuille par ligne)\n");
		bLongChargee.afficheSeqArbre();

		/*
		// On essai de chercher une ligne du fichier en partant d'un num de sécu
		System.out.println("\n**********************************************************************************");
		System.out.println("\nRecherche d'une ligne du fichier à partir d'un n° de secu et de l'index : \n");
		System.out.println(bLongChargee.chercherLigne(169313560050258L, indexChargee, "BD.txt"));

		System.out.println("\n**********************************************************************************");
		System.out.println("\nRecherche séquencielle d'une ligne du fichier : \n");
		System.out.println(main.rechSeqFich(169313560050258L, "BD.txt"));
		*/

		// Comparaison du temps de recherhce entre les deux approches
		System.out.println("\n\n**********************************************************************************");
		System.out.println("**********************************************************************************");
		System.out.println("\nComparaison du temps de recherhce entre les deux approches :\n");

		int tailleEchantillon = 100;
		long numSecu;
		long tExecRechSeq[] = new long[tailleEchantillon];
		long tExecRechIndex[] = new long[tailleEchantillon];

		// On lance la recherche de 100 valeurs differentes avec les deux approches et on stocke à chaque recherche
		// et pour chaqu'une des approches le temps d'éxecution dans un tableau
		String line = "";
		for (int i=0; i<tailleEchantillon; i++) {
			numSecu = (long) (indexChargee2.get((int) (Math.random() * 10000)));

			System.out.println("Recherche séquencielle de la valeur " + numSecu + " :\n");
			long start1 = System.currentTimeMillis();
			ligne = main.rechSeqFich(numSecu, "BD.txt");
			long end1 = System.currentTimeMillis();
			System.out.println("Résultat : " + line);
			tExecRechSeq[i] = end1-start1;
			System.out.println("Temps de recherche : " + tExecRechSeq[i] + " ms\n\n");

			System.out.println("Recherche avec l'index de la valeur " + numSecu + " :\n");
			long start2 = System.currentTimeMillis();
			ligne = bLongChargee.chercherLigne(numSecu, indexChargee, "BD.txt");
			long end2 = System.currentTimeMillis();
			System.out.println("Résultat : " + line);
			tExecRechIndex[i] = end2 - start2;
			System.out.println("Temps de recherche : " + tExecRechIndex[i] + " ms\n**************************************" +
					"***********************************\n");
		}

		// On calcule le min, le max et la moyenne pour chaqu'une des deux approches
		long min1 = tExecRechSeq[0], min2 = tExecRechIndex[0], max1 = tExecRechSeq[0], max2 =tExecRechIndex[0];
		double moy1 = 0, moy2 = 0;
		for (int i = 0; i<tailleEchantillon; i++) {
			moy1 += tExecRechSeq[i];
			moy2 += tExecRechIndex[i];

			if (tExecRechSeq[i] < min1) min1 = tExecRechSeq[i];
			if (tExecRechIndex[i] < min2) min2 = tExecRechIndex[i];

			if (tExecRechSeq[i] > max1) max1 = tExecRechSeq[i];
			if (tExecRechIndex[i] > max2) max2 = tExecRechIndex[i];
		}
		moy1 = moy1/tailleEchantillon;
		moy2 = moy2/tailleEchantillon;

		System.out.println("\n*****************************************************************************");
		System.out.println("\nStatistiques \n");

		System.out.println("\nTemps de recherche séquenciel dans le fichier :\n\nmin =  " + min1 + " ms\nmax = "
		+ max1 + " ms\nmoyenne = " + moy1 + " ms");

		System.out.println("\n\nTemps de recherche par l'index :\n\nmin =  " + min2 + " ms\nmax = "
				+ max2 + " ms\nmoyenne = " + moy2 + " ms");



		/*
		TestString test = new TestString();
		Noeud<String> noeud = new Noeud<String>(2, 5,test, null);
		Noeud<String> noeud1 = new Noeud<String>(2, 5,test, null);
		Noeud<String> noeud2 = new Noeud<String>(2, 5,test, null);
		*/
    }
}
