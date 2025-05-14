package gestionnairetaches;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principale du gestionnaire de tâches
 */
public class Application {
    private static Scanner scanner = new Scanner(System.in);
    private static GestionnaireTaches gestionnaire = new GestionnaireTaches();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public static void main(String[] args) {
        // Charger les tâches existantes
        gestionnaire.chargerTaches();
        
        boolean quitter = false;
        
        while (!quitter) {
            afficherMenu();
            int choix = lireChoix();
            
            switch (choix) {
                case 1:
                    ajouterNouvelleTache();
                    break;
                case 2:
                    marquerTacheTerminee();
                    break;
                case 3:
                    supprimerTache();
                    break;
                case 4:
                    afficherToutesLesTaches();
                    break;
                case 5:
                    afficherTachesEnCours();
                    break;
                case 6:
                    afficherTachesTerminees();
                    break;
                case 7:
                    sauvegarderEtQuitter();
                    quitter = true;
                    break;
                default:
                    System.out.println("Choix non valide. Veuillez réessayer.");
            }
        }
    }
    
    /**
     * Affiche le menu principal
     */
    private static void afficherMenu() {
        System.out.println("\n=== GESTIONNAIRE DE TÂCHES ===");
        System.out.println("1. Ajouter une nouvelle tâche");
        System.out.println("2. Marquer une tâche comme terminée");
        System.out.println("3. Supprimer une tâche");
        System.out.println("4. Afficher toutes les tâches");
        System.out.println("5. Afficher les tâches en cours");
        System.out.println("6. Afficher les tâches terminées");
        System.out.println("7. Sauvegarder et quitter");
        System.out.print("Votre choix: ");
    }
    
    /**
     * Lit le choix de l'utilisateur
     * 
     * @return Le choix sous forme d'entier
     */
    private static int lireChoix() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;  // Valeur invalide qui sera gérée par le switch
        }
    }
    
    /**
     * Ajoute une nouvelle tâche
     */
    private static void ajouterNouvelleTache() {
        System.out.println("\n=== AJOUTER UNE NOUVELLE TÂCHE ===");
        
        System.out.print("Titre: ");
        String titre = scanner.nextLine();
        
        System.out.print("Description: ");
        String description = scanner.nextLine();
        
        LocalDate dateEcheance = null;
        boolean dateValide = false;
        
        while (!dateValide) {
            System.out.print("Date d'échéance (jj/mm/aaaa): ");
            String dateStr = scanner.nextLine();
            
            try {
                dateEcheance = LocalDate.parse(dateStr, FORMATTER);
                dateValide = true;
            } catch (DateTimeParseException e) {
                System.out.println("Format de date invalide. Utilisez le format jj/mm/aaaa.");
            }
        }
        
        Tache nouvelleTache = gestionnaire.ajouterTache(titre, description, dateEcheance);
        System.out.println("Tâche ajoutée avec succès: " + nouvelleTache);
    }
    
    /**
     * Marque une tâche comme terminée
     */
    private static void marquerTacheTerminee() {
        List<Tache> tachesEnCours = gestionnaire.filtrerTaches(false);
        
        if (tachesEnCours.isEmpty()) {
            System.out.println("Il n'y a pas de tâches en cours.");
            return;
        }
        
        System.out.println("\n=== MARQUER UNE TÂCHE COMME TERMINÉE ===");
        
        for (int i = 0; i < tachesEnCours.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, tachesEnCours.get(i));
        }
        
        System.out.print("Entrez le numéro de la tâche à terminer: ");
        int index = lireChoix() - 1;
        
        if (index >= 0 && index < tachesEnCours.size()) {
            Tache tache = tachesEnCours.get(index);
            tache.terminer();
            System.out.println("Tâche marquée comme terminée: " + tache);
        } else {
            System.out.println("Numéro de tâche invalide.");
        }
    }
    
    /**
     * Supprime une tâche
     */
    private static void supprimerTache() {
        List<Tache> toutesLesTaches = gestionnaire.getTaches();
        
        if (toutesLesTaches.isEmpty()) {
            System.out.println("Il n'y a pas de tâches à supprimer.");
            return;
        }
        
        System.out.println("\n=== SUPPRIMER UNE TÂCHE ===");
        
        for (int i = 0; i < toutesLesTaches.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, toutesLesTaches.get(i));
        }
        
        System.out.print("Entrez le numéro de la tâche à supprimer: ");
        int index = lireChoix() - 1;
        
        if (gestionnaire.supprimerTache(index)) {
            System.out.println("Tâche supprimée avec succès.");
        } else {
            System.out.println("Numéro de tâche invalide.");
        }
    }
    
    /**
     * Affiche toutes les tâches
     */
    private static void afficherToutesLesTaches() {
        List<Tache> toutesLesTaches = gestionnaire.getTaches();
        
        System.out.println("\n=== TOUTES LES TÂCHES ===");
        
        if (toutesLesTaches.isEmpty()) {
            System.out.println("Aucune tâche n'est enregistrée.");
        } else {
            for (int i = 0; i < toutesLesTaches.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, toutesLesTaches.get(i));
            }
        }
    }
    
    /**
     * Affiche les tâches en cours
     */
    private static void afficherTachesEnCours() {
        List<Tache> tachesEnCours = gestionnaire.filtrerTaches(false);
        
        System.out.println("\n=== TÂCHES EN COURS ===");
        
        if (tachesEnCours.isEmpty()) {
            System.out.println("Aucune tâche en cours.");
        } else {
            for (int i = 0; i < tachesEnCours.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, tachesEnCours.get(i));
            }
        }
    }
    
    /**
     * Affiche les tâches terminées
     */
    private static void afficherTachesTerminees() {
        List<Tache> tachesTerminees = gestionnaire.filtrerTaches(true);
        
        System.out.println("\n=== TÂCHES TERMINÉES ===");
        
        if (tachesTerminees.isEmpty()) {
            System.out.println("Aucune tâche terminée.");
        } else {
            for (int i = 0; i < tachesTerminees.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, tachesTerminees.get(i));
            }
        }
    }
    
    /**
     * Sauvegarde les tâches et quitte l'application
     */
    private static void sauvegarderEtQuitter() {
        if (gestionnaire.sauvegarderTaches()) {
            System.out.println("Tâches sauvegardées avec succès.");
        } else {
            System.out.println("Erreur lors de la sauvegarde des tâches.");
        }
        System.out.println("Merci d'avoir utilisé le Gestionnaire de Tâches. À bientôt !");
    }
}