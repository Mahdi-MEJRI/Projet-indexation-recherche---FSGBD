package fr.miage.fsgbd;


import com.sun.source.tree.Tree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
        /*
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //On cr�e une nouvelle instance de notre JDialog
                GUI fenetre = new GUI();
                fenetre.setVisible(true);
            }
        });
        */


		Map index1 = new HashMap();

		// Les valeurs de num de s�cu ne sont pas tri�es donc on les met dans un TreeMap
		Map index2 = new TreeMap();

		Main main = new Main();


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

		// On sauve l'arbre et l'index pour faire les tests sur le temps de recherche
		BSerializer<Long> save = new BSerializer<Long>(bLong, "arbre.abr");

		IndexSerializer saveIndex = new IndexSerializer(index2, "index.abr");


		// Si on met tout le code de la methode main qui precede cette phrase en commentaire, on peut faire des traitements
		// sur l'arbre precedent (serialise) sans generer un nouveau fichier et un nouvel arbre et un nouvel index

		System.out.println("**********************************************************************************");
		System.out.println("**********************************************************************************");
		System.out.println("Déserialisation de l'arbre et de l'index ...");

		// On charge l'arbre et l'index
		BDeserializer<Long> load = new BDeserializer<Long>();
		fr.miage.fsgbd.BTreePlus<Long> bLongChargee = load.getArbre("arbre.abr");

		IndexDeserializer loadIndex = new IndexDeserializer();
		TreeMap indexChargee = (TreeMap) loadIndex.getIndex("index.abr");



		System.out.println("**********************************************************************************");
		System.out.println("Affichage de l'arbre");
		bLongChargee.afficheArbre();

		// On essai de chercher une ligne du fichier en partant d'un num de sécu
		System.out.println("**********************************************************************************");
		System.out.println("Recherche d'une ligne du fichier à partir d'un n° de secu et de l'index : \n");
		System.out.println(bLongChargee.chercherLigne(101917020893904L, indexChargee, "BD.txt"));

		// Affichage séquenciel des feuilles de l'arbre (une feuille par ligne)
		System.out.println("**********************************************************************************");
		System.out.println("Affichage séquenciel des feuilles (une feuille par ligne)");
		bLongChargee.afficheSeqArbre();

		System.out.println("**********************************************************************************");
		System.out.println("Recherche séquencielle d'une ligne du fichier : \n");
		System.out.println(main.rechSeqFich(101917020893904L, "BD.txt"));




		/*
		TestString test = new TestString();
		Noeud<String> noeud = new Noeud<String>(2, 5,test, null);
		Noeud<String> noeud1 = new Noeud<String>(2, 5,test, null);
		Noeud<String> noeud2 = new Noeud<String>(2, 5,test, null);
		*/
    }
}
