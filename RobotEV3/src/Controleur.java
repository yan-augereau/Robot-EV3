import java.util.Random;
import lejos.hardware.port.*;
import java.io.*;
import java.net.SocketException;
import java.net.UnknownHostException;

import lejos.hardware.Bluetooth;
import lejos.hardware.Button;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.BTConnector;
import lejos.remote.nxt.NXTConnection;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;




/**
 * Classe Controleur
 * @author M1 MIAGE Alternance : AUGEREAU Yan, CELLIER ALexandre, MEUNIER Matthias & PERRET Pierre-Yves
 * @version 2.1
 */
public class Controleur {
	
	private static BTConnection BTLink;
	private static DataOutputStream donneeSortie; 
	private static DataInputStream donneeEntree;
	private static int line=0;
	private static DataInputStream objetA;
	private static DataInputStream objetB;
	private static DataInputStream pile;


	/**
	 * Cette méthode permet d'initialiser tous les moteurs et capteurs du robot
	 * @param mr Le moteur de rotation
	 * @param mb Le moteur du bras
	 * @param mp Le moteur de la pince
	 * @param cr Le capteur de rotation
	 * @param cb Le capteur du bras
	 */
	public void initialisation(Moteur mr, Moteur mb, MoteurPince mp, Capteur cr, Capteur cb) {
		mb.initialisationBras(cb);
		mr.initialisationRotation(cr);
		mp.initialisationPince();
		
		//System.out.println("Moteurs initialisés");
	}
	

	
	/**
	 * Cette méthode permet de savoir s'il y a une distance suffisante entre deux objets
	 * @param A Objet A
	 * @param B Objet B
	 * @return Retourne vrai si la position est suffisante, faux sinon
	 */
	public boolean distanceOk(int A, int B) {
		boolean res = false;
		if(Math.abs(A - B) > 120) {
			res = true;
		}
		return res;
	}
	
	/**
	 * Cette méthode permet de générer un entier aléatoire compris entre deux bornes
	 * @param borneInf Borne inférieure
	 * @param borneSup Borne supérieure
	 * @return Retourne l'entier aléatoire
	 */
	public int genererInt(int borneInf, int borneSup){
		   Random random = new Random();
		   int nb;
		   nb = random.nextInt(borneSup-borneInf);
		   return nb;
		}
	
	/**
	 * Cette méthode permet d'intervertir deux objets. L'objet A va aller à la place del'objet B et inversement.
	 * @param mr Moteur de rotation
	 * @param mb Moteur du bras
	 * @param mp Moteur de la pince
	 * @param cr Capteur de rotation
	 * @param cb Cpateur du bars
	 * @param positionA Posiiton de l'objet A
	 * @param positionB Position de l'objet B
	 */
	public void inverserObjets(Moteur mr, Moteur mb, MoteurPince mp, Capteur cr, Capteur cb, int positionA, int positionB) {
		int tmp = genererInt(0,mr.angleMax);
		if(positionA >= 0 && positionA <= mr.angleMax && positionB >= 0 && positionB <= mr.angleMax) {
			if(distanceOk(positionA, positionB)) {
				while(distanceOk(tmp,positionA) == false || distanceOk(tmp,positionB) == false) {
					tmp = genererInt(0,mr.angleMax);
				}
				
				
				//System.out.println("TMP : "+tmp);
				
				mr.pivoter(positionA);
				mb.baisserBras(270);
				mp.fermer();
				mb.initialisationBras(cb);
				// L'objet A est saisi
				
				mr.pivoter(tmp-positionA);
				mb.baisserBras(270);
				mp.ouvrir();
				mb.initialisationBras(cb);
				// L'objet A est déposé en tmp
				
				mr.pivoter(positionB-tmp);
				mb.baisserBras(270);
				mp.fermer();
				mb.initialisationBras(cb);
				// L'objet B est saisi
				
				mr.pivoter(positionA-positionB);
				mb.baisserBras(270);
				mp.ouvrir();
				mb.initialisationBras(cb);
				// L'objet B est dépose en A
				
				mr.pivoter(tmp-positionA);
				mb.baisserBras(270);
				mp.fermer();
				mb.initialisationBras(cb);
				// L'objet A est saisi
				
				mr.pivoter(positionB-tmp);
				mb.baisserBras(270);
				mp.ouvrir();
				mb.initialisationBras(cb);
				// L'objet A est dépose en B
				
				this.initialisation(mr, mb, mp, cr, cb);
				
			} else {
				System.out.println("Les deux objets sont trop proches l un de l autre");
			}
		} else {
			System.out.println("Les positions sont hors champs pour le moteur de rotation");
		}
		
		
	}
	
	/**
	 * Cette méthode permet de dépiler une pile de deux objets
	 * @param mr Moteur de rotation
	 * @param mb Moteur du bras
	 * @param mp Moteur de la pince
	 * @param cr Capteur de rotation
	 * @param cb Capteur du bras
	 * @param position Pile Position de la pile d'objets
	 * @param positionA Future position de l'objet se trouvant au sommet de la pile
	 * @param positionB Future position de l'objet se trouvant à la base de la pile
	 */
	public void depiler(Moteur mr, Moteur mb, MoteurPince mp, Capteur cr, Capteur cb, int positionPile, int positionA, int positionB) {
		if(positionA >= 0 && positionA <= mr.angleMax && positionB >= 0 && positionB <= mr.angleMax && positionPile >= 0 && positionPile <= mr.angleMax) {
			if(distanceOk(positionPile, positionA) && distanceOk(positionPile, positionB) && distanceOk(positionA,positionB)) {
				
				mr.pivoter(positionPile);
				mb.baisserBras(215);
				mp.fermer();
				mb.initialisationBras(cb);
				// L'objet au sommet de la pile est saisi
				
				mr.pivoter(positionA-positionPile);
				mb.baisserBras(270);
				mp.ouvrir();
				mb.initialisationBras(cb);
				// L'objet du sommet de la pile est déposé en position A
				
				mr.pivoter(positionPile-positionA);
				mb.baisserBras(270);
				mp.fermer();
				mb.initialisationBras(cb);
				// L'objet en bas de la pile est saisi
				
				mr.pivoter(positionB-positionPile);
				mb.baisserBras(270);
				mp.ouvrir();
				mb.initialisationBras(cb);
				
				this.initialisation(mr, mb, mp, cr, cb);
				
			} else {
				System.out.println("Positions sont trop proches");
			}
			
		} else {
			System.out.println("Les positions sont hors champs pour le moteur de rotation");
		}
		
		
		
	}

	public static void bluetoothConnection(){  
	    System.out.println("En ecoute");
	    BTConnector nxtCommConnector = (BTConnector) Bluetooth.getNXTCommConnector();
	    BTLink = (BTConnection) nxtCommConnector.waitForConnection(6000, NXTConnection.RAW);
	    donneeSortie = BTLink.openDataOutputStream();
	    donneeEntree = BTLink.openDataInputStream();
	    objetA = BTLink.openDataInputStream();
	    objetB = BTLink.openDataInputStream();
	    pile = BTLink.openDataInputStream();
	}

	
	
	public static void main(String[] args) throws IOException, InterruptedException, SocketException {
		Controleur controleur = new Controleur();
		Capteur capteurRotation = new Capteur(SensorPort.S1);
		Capteur capteurBras = new Capteur(SensorPort.S3);
		Moteur moteurBras = new Moteur(MotorPort.B, 270);
		Moteur moteurRotation = new Moteur(MotorPort.C, 615);
		MoteurPince moteurP = new MoteurPince(MotorPort.A);
		
		controleur.initialisation(moteurRotation, moteurBras, moteurP, capteurRotation, capteurBras);
		
		bluetoothConnection();
		
		boolean connexion = true;
		int p = -1;
		int a = -1;
		int b = -1;
		boolean ok = false;
		
		
		while(connexion) {
			
			line = donneeEntree.readByte();

			
			switch(line) {
			  // Initialisation des moteurs du robot
			  case 0:
			    controleur.initialisation(moteurRotation, moteurBras, moteurP, capteurRotation, capteurBras);
			    break;
			  // Echanger des objets  
			  case 1:
				  System.out.println("ECHANGER");
				  System.out.println("Reception en cours ...");
				  line = -1;
				  
				  while(line == -1) {
					  line = objetA.readByte();
					  a = line;
					  for(int i = 1; i <= 4; i++) {
						  line = objetA.readByte();
						  a = a + line;
					  }
				  } 
				  line = -1;
				  while(line == -1) {
					  line = objetB.readByte();
					  b = line;
					  for(int j = 1;j <= 4;j++) {
						  line = objetB.readByte();
						  b = b + line;
					  }
				  }
				  

				  
				  System.out.println("Position A : "+a);
				  System.out.println("Position B : "+b);
				  controleur.inverserObjets(moteurRotation, moteurBras, moteurP, capteurRotation, capteurBras, a, b);
				  
			    break;
			  // Dépiler des objets  
			  case 2:
				  System.out.println("DEPILER");
				  System.out.println("Reception en cours ...");
				  line = -1;
				  while(line == -1) {
					  line = pile.readByte();
					  p = line;
					  for(int i = 1; i <= 4; i++) {
						  line = pile.readByte();
						  p = p + line;
					  }
				  } 
				  line = -1;
				  while(line == -1) {
					  line = objetA.readByte();
					  a = line;
					  for(int i = 1; i <= 4; i++) {
						  line = objetA.readByte();
						  a = a + line;
					  }
				  } 
				  line = -1;
				  while(line == -1) {
					  line = objetB.readByte();
					  b = line;
					  for(int j = 1;j <= 4;j++) {
						  line = objetB.readByte();
						  b = b + line;
					  }
				  }
				  
				  

				  System.out.println("Position Pile : "+p);
				  System.out.println("Position A : "+a);
				  System.out.println("Position B : "+b);
				  	
				  controleur.depiler(moteurRotation, moteurBras, moteurP, capteurRotation, capteurBras, p, a, b);
				break;
			  case 3:
				  objetA.close();
				  objetB.close();
				  pile.close();
				  donneeEntree.close();
				  donneeSortie.close();
				  BTLink.close();
				  connexion = false;
			}
			
		}	
	}
}