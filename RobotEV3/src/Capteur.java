import lejos.hardware.sensor.*;
import lejos.hardware.port.Port;

/**
 * Classe Capteur héritée de EV3TouchSensor. Elle est utilisée pour les dux capteurs du robot (bras et rotation)
 * @author M1 MIAGE Alternance : AUGEREAU Yan, CELLIER ALexandre, MEUNIER Matthias & PERRET Pierre-Yves
 * @version 2.1
 */
public class Capteur extends EV3TouchSensor{


	/**
	 * Constructeur pour le capteur de contact
	 * @param port Port sur lequel le le capteur est connecté à la brique EV3
	 */
	public Capteur(Port port) {
		super(port);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Cette méthode permer de retourner vrai si un capteur est enfoncé. Elle retourne faux s'il n'est pas enfoncé.
	 */
	public boolean contact() {
		boolean res = false;
		float etat = 0;
		float[] tab = {0,1};
		this.fetchSample(tab, 0);
		etat = tab[0];
		if(etat == 1) {
			res= true;
		}
		return res;
	}
	
}

