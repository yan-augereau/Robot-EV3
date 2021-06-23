import lejos.hardware.motor.*;
import lejos.hardware.port.Port;

/**
 * Classe MoteurPince héritée de EV3MediumRegulatedMotor. Elle est utilisée pour le moteur de la pince du robot.
 * @author yanaugereau
 * @version 2.1
 */
public class MoteurPince extends EV3MediumRegulatedMotor{
	
	public EnumEtatPince etat;
	public int position;
	
	/**
	 * Constructeur pour le moteur de la pince
	 * @param port Port sur lequel le moteur est connecté à la brique EV3.
	 */
	public MoteurPince(Port port) {
		super(port);
		this.etat = EnumEtatPince.ouverte;
		this.position = 0;

	}
	
	/**
	 * Cette méthode permet de fermer la pince du robot
	 */
	public void fermer() {
		int angle = 100;
		if(this.etat == EnumEtatPince.ouverte) {
			this.rotate(angle);
			this.etat = EnumEtatPince.fermee;
			this.stop();
		}	
	}
	
	/**
	 * Cette méthode permet d'ouvrir la pince du robot
	 */
	public void ouvrir() {
		int angle = 100;
		if(this.etat == EnumEtatPince.fermee) {
			this.rotate(-angle);
			this.etat = EnumEtatPince.ouverte;
			this.stop();
		}	
	}
	
	/**
	 * Cette méthode permet d'initialiser la pince dans une certaine position. La pince est initialisée quand elle est ouverte.
	 */
	public void initialisationPince() {
		if(this.etat == EnumEtatPince.fermee) {
			this.ouvrir();
			
		}
		//eSystem.out.println("Pince initialisée");
	}
	

}
