package gestionnairetaches;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Classe représentant une tâche avec un titre, une description,
 * une date d'échéance et un statut (terminée ou non).
 */
public class Tache {
    private String titre;
    private String description;
    private LocalDate dateEcheance;
    private boolean terminee;
    
    // Formatter pour afficher les dates en format français
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Constructeur
     * 
     * @param titre Le titre de la tâche
     * @param description La description détaillée de la tâche
     * @param dateEcheance La date limite pour terminer la tâche
     */
    public Tache(String titre, String description, LocalDate dateEcheance) {
        this.titre = titre;
        this.description = description;
        this.dateEcheance = dateEcheance;
        this.terminee = false; // Par défaut, une nouvelle tâche n'est pas terminée
    }
    
    // Getters et setters
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getDateEcheance() {
        return dateEcheance;
    }
    
    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }
    
    public boolean isTerminee() {
        return terminee;
    }
    
    public void setTerminee(boolean terminee) {
        this.terminee = terminee;
    }
    
    /**
     * Marque la tâche comme terminée
     */
    public void terminer() {
        this.terminee = true;
    }
    
    /**
     * Représentation textuelle de la tâche
     */
    @Override
    public String toString() {
        String statut = terminee ? "Terminée" : "En cours";
        return String.format("[%s] %s - échéance: %s\n  %s", 
                statut, titre, dateEcheance.format(FORMATTER), description);
    }
}