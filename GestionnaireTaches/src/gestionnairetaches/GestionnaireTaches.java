package gestionnairetaches;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe qui gère une collection de tâches
 */
public class GestionnaireTaches {
    private List<Tache> taches;
    private static final String FICHIER_SAUVEGARDE = "taches.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Constructeur
     */
    public GestionnaireTaches() {
        this.taches = new ArrayList<>();
    }
    
    /**
     * Ajoute une nouvelle tâche à la liste
     * 
     * @param titre Le titre de la tâche
     * @param description La description de la tâche
     * @param dateEcheance La date d'échéance
     * @return La tâche créée
     */
    public Tache ajouterTache(String titre, String description, LocalDate dateEcheance) {
        Tache nouvelleTache = new Tache(titre, description, dateEcheance);
        taches.add(nouvelleTache);
        return nouvelleTache;
    }
    
    /**
     * Supprime une tâche de la liste
     * 
     * @param index L'index de la tâche à supprimer
     * @return true si la tâche a été supprimée, false sinon
     */
    public boolean supprimerTache(int index) {
        if (index >= 0 && index < taches.size()) {
            taches.remove(index);
            return true;
        }
        return false;
    }
    
    /**
     * Marque une tâche comme terminée
     * 
     * @param index L'index de la tâche à marquer comme terminée
     * @return true si la tâche a été marquée comme terminée, false sinon
     */
    public boolean terminerTache(int index) {
        if (index >= 0 && index < taches.size()) {
            taches.get(index).terminer();
            return true;
        }
        return false;
    }
    
    /**
     * Retourne toutes les tâches
     * 
     * @return La liste de toutes les tâches
     */
    public List<Tache> getTaches() {
        return new ArrayList<>(taches);  // Retourne une copie pour éviter la modification directe
    }
    
    /**
     * Filtre les tâches en fonction de leur statut
     * 
     * @param terminee true pour obtenir les tâches terminées, false pour les tâches en cours
     * @return Une liste des tâches filtrées
     */
    public List<Tache> filtrerTaches(boolean terminee) {
        return taches.stream()
                .filter(t -> t.isTerminee() == terminee)
                .collect(Collectors.toList());
    }
    
    /**
     * Sauvegarde les tâches dans un fichier CSV
     * 
     * @return true si la sauvegarde a réussi, false sinon
     */
    public boolean sauvegarderTaches() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FICHIER_SAUVEGARDE))) {
            // En-tête du fichier CSV
            writer.write("Titre,Description,DateEcheance,Terminee");
            writer.newLine();
            
            // Écriture de chaque tâche
            for (Tache tache : taches) {
                String ligne = String.format("%s,%s,%s,%s",
                        tache.getTitre(),
                        tache.getDescription().replace(",", ";"),  // Remplace les virgules pour éviter les problèmes CSV
                        tache.getDateEcheance().format(FORMATTER),
                        tache.isTerminee());
                writer.write(ligne);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des tâches: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Charge les tâches depuis un fichier CSV
     * 
     * @return true si le chargement a réussi, false sinon
     */
    public boolean chargerTaches() {
        taches.clear();  // Vider la liste actuelle
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FICHIER_SAUVEGARDE))) {
            // Ignorer la première ligne (en-tête)
            String ligne = reader.readLine();
            
            // Lire chaque ligne et créer une tâche
            while ((ligne = reader.readLine()) != null) {
                String[] donnees = ligne.split(",");
                if (donnees.length >= 4) {
                    String titre = donnees[0];
                    String description = donnees[1];
                    LocalDate dateEcheance = LocalDate.parse(donnees[2], FORMATTER);
                    boolean terminee = Boolean.parseBoolean(donnees[3]);
                    
                    Tache tache = ajouterTache(titre, description, dateEcheance);
                    if (terminee) {
                        tache.terminer();
                    }
                }
            }
            return true;
        } catch (IOException e) {
            // Si le fichier n'existe pas encore, ce n'est pas une erreur
            if (e.getMessage().contains("No such file")) {
                return true;
            }
            System.err.println("Erreur lors du chargement des tâches: " + e.getMessage());
            return false;
        }
    }
}