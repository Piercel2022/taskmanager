package gestionnairetaches;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Application GUI pour le gestionnaire de tâches utilisant JavaFX
 * Compatible avec Maven et les modules JavaFX
 */
public class TaskManagerApplication extends Application {
    
    private GestionnaireTaches gestionnaire = new GestionnaireTaches();
    private ListView<Tache> listViewTaches;
    private ObservableList<Tache> observableListTaches;
    private Label labelStatut;
    private ComboBox<String> comboFiltre;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger les tâches existantes
            gestionnaire.chargerTaches();
            
            // Configuration de la fenêtre principale
            primaryStage.setTitle("Gestionnaire de Tâches - Maven");
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
            // Création de l'interface
            VBox root = new VBox(10);
            root.setPadding(new Insets(15));
            root.setStyle("-fx-background-color: #f8f9fa;");
            
            // Titre
            Label titre = new Label("GESTIONNAIRE DE TÂCHES");
            titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            titre.setAlignment(Pos.CENTER);
            
            // Barre d'outils
            HBox barreOutils = creerBarreOutils();
            
            // Zone de filtrage
            HBox zoneFiltrage = creerZoneFiltrage();
            
            // Liste des tâches
            VBox zoneListe = creerZoneListe();
            
            // Barre de statut
            labelStatut = new Label("Application démarrée avec Maven");
            labelStatut.setStyle("-fx-padding: 8px; -fx-background-color: #e3f2fd; -fx-border-color: #2196f3; -fx-border-radius: 4px; -fx-background-radius: 4px;");
            
            // Assemblage de l'interface
            root.getChildren().addAll(titre, barreOutils, zoneFiltrage, zoneListe, labelStatut);
            VBox.setVgrow(zoneListe, Priority.ALWAYS);
            
            // Création de la scène
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            
            // Gestion de la fermeture
            primaryStage.setOnCloseRequest(e -> {
                sauvegarderTaches();
                System.out.println("Application fermée proprement");
            });
            
            // Mise à jour initiale
            mettreAJourListe();
            
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage de l'application : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crée la barre d'outils avec les boutons principaux
     */
    private HBox creerBarreOutils() {
        HBox barre = new HBox(10);
        barre.setAlignment(Pos.CENTER);
        barre.setPadding(new Insets(10));
        barre.setStyle("-fx-background-color: #34495e; -fx-background-radius: 8px;");
        
        Button btnAjouter = creerBouton("Ajouter Tâche", "#27ae60", e -> ajouterTache());
        Button btnTerminer = creerBouton("Marquer Terminée", "#3498db", e -> marquerTerminee());
        Button btnSupprimer = creerBouton("Supprimer", "#e74c3c", e -> supprimerTache());
        Button btnSauvegarder = creerBouton("Sauvegarder", "#f39c12", e -> sauvegarderTaches());
        
        barre.getChildren().addAll(btnAjouter, btnTerminer, btnSupprimer, btnSauvegarder);
        
        return barre;
    }
    
    /**
     * Crée un bouton stylisé
     */
    private Button creerBouton(String texte, String couleur, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button btn = new Button(texte);
        btn.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-padding: 10px 15px; -fx-background-radius: 5px; -fx-cursor: hand;", couleur));
        btn.setOnAction(action);
        
        // Effet hover
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-opacity: 0.8;"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-opacity: 0.8;", "")));
        
        return btn;
    }
    
    /**
     * Crée la zone de filtrage
     */
    private HBox creerZoneFiltrage() {
        HBox zone = new HBox(10);
        zone.setAlignment(Pos.CENTER_LEFT);
        zone.setPadding(new Insets(10));
        zone.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        
        Label labelFiltre = new Label("Filtrer les tâches :");
        labelFiltre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        comboFiltre = new ComboBox<>();
        comboFiltre.getItems().addAll("Toutes les tâches", "Tâches en cours", "Tâches terminées");
        comboFiltre.setValue("Toutes les tâches");
        comboFiltre.setStyle("-fx-font-size: 12px;");
        comboFiltre.setOnAction(e -> mettreAJourListe());
        
        zone.getChildren().addAll(labelFiltre, comboFiltre);
        
        return zone;
    }
    
    /**
     * Crée la zone d'affichage de la liste des tâches
     */
    private VBox creerZoneListe() {
        VBox zone = new VBox(10);
        
        Label labelListe = new Label("📋 Liste des tâches :");
        labelListe.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        
        observableListTaches = FXCollections.observableArrayList();
        listViewTaches = new ListView<>(observableListTaches);
        listViewTaches.setPrefHeight(400);
        listViewTaches.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5px;");
        
        // Personnalisation de l'affichage des tâches
        listViewTaches.setCellFactory(listView -> new ListCell<Tache>() {
            @Override
            protected void updateItem(Tache tache, boolean empty) {
                super.updateItem(tache, empty);
                if (empty || tache == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(formatTache(tache));
                    if (tache.estTerminee()) {
                        setStyle("-fx-background-color: #d5f4e6; -fx-text-fill: #27ae60; -fx-padding: 8px;");
                    } else {
                        LocalDate aujourd = LocalDate.now();
                        if (tache.getDateEcheance().isBefore(aujourd)) {
                            setStyle("-fx-background-color: #fadbd8; -fx-text-fill: #e74c3c; -fx-padding: 8px;");
                        } else {
                            setStyle("-fx-background-color: #ebf3fd; -fx-text-fill: #2c3e50; -fx-padding: 8px;");
                        }
                    }
                }
            }
        });
        
        zone.getChildren().addAll(labelListe, listViewTaches);
        VBox.setVgrow(listViewTaches, Priority.ALWAYS);
        
        return zone;
    }
    
    /**
     * Formate l'affichage d'une tâche
     */
    private String formatTache(Tache tache) {
        String statut = tache.estTerminee() ? "✅" : "⏳";
        return String.format("%s %s - %s (Échéance: %s)", 
                           statut, tache.getTitre(), tache.getDescription(), tache.getDateEcheance());
    }
    
    /**
     * Ajoute une nouvelle tâche
     */
    private void ajouterTache() {
        Dialog<Tache> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Tâche");
        dialog.setHeaderText("Créer une nouvelle tâche");
        
        // Boutons
        ButtonType buttonTypeOk = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);
        
        // Champs de saisie
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titreField = new TextField();
        titreField.setPromptText("Titre de la tâche");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description détaillée");
        descriptionArea.setPrefRowCount(3);
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now().plusDays(1));
        
        grid.add(new Label("Titre :"), 0, 0);
        grid.add(titreField, 1, 0);
        grid.add(new Label("Description :"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Échéance :"), 0, 2);
        grid.add(datePicker, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // Validation
        Node addButton = dialog.getDialogPane().lookupButton(buttonTypeOk);
        addButton.setDisable(true);
        titreField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty());
        });
        
        // Focus initial
        titreField.requestFocus();
        
        // Conversion du résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                return gestionnaire.ajouterTache(
                    titreField.getText().trim(),
                    descriptionArea.getText().trim(),
                    datePicker.getValue()
                );
            }
            return null;
        });
        
        Optional<Tache> result = dialog.showAndWait();
        if (result.isPresent()) {
            mettreAJourListe();
            labelStatut.setText("✅ Tâche créée : " + result.get().getTitre());
        }
    }
    
    /**
     * Marque une tâche comme terminée
     */
    private void marquerTerminee() {
        Tache tacheSelectionnee = listViewTaches.getSelectionModel().getSelectedItem();
        if (tacheSelectionnee == null) {
            afficherAlerte("Sélection requise", "Veuillez sélectionner une tâche à terminer.");
            return;
        }
        
        if (tacheSelectionnee.estTerminee()) {
            afficherAlerte("Tâche déjà terminée", "Cette tâche est déjà marquée comme terminée.");
            return;
        }
        
        tacheSelectionnee.terminer();
        mettreAJourListe();
        labelStatut.setText("✅ Tâche terminée : " + tacheSelectionnee.getTitre());
    }
    
    /**
     * Supprime une tâche
     */
    private void supprimerTache() {
        Tache tacheSelectionnee = listViewTaches.getSelectionModel().getSelectedItem();
        if (tacheSelectionnee == null) {
            afficherAlerte("Sélection requise", "Veuillez sélectionner une tâche à supprimer.");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer la tâche");
        confirmation.setContentText("Voulez-vous vraiment supprimer :\n" + tacheSelectionnee.getTitre() + " ?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int index = gestionnaire.getTaches().indexOf(tacheSelectionnee);
            if (gestionnaire.supprimerTache(index)) {
                mettreAJourListe();
                labelStatut.setText("🗑️ Tâche supprimée : " + tacheSelectionnee.getTitre());
            }
        }
    }
    
    /**
     * Sauvegarde les tâches
     */
    private void sauvegarderTaches() {
        if (gestionnaire.sauvegarderTaches()) {
            labelStatut.setText("💾 Tâches sauvegardées avec succès");
        } else {
            labelStatut.setText("❌ Erreur lors de la sauvegarde");
        }
    }
    
    /**
     * Met à jour la liste affichée selon le filtre sélectionné
     */
    private void mettreAJourListe() {
        observableListTaches.clear();
        
        String filtreSelectionne = comboFiltre.getValue();
        switch (filtreSelectionne) {
            case "Tâches en cours":
                observableListTaches.addAll(gestionnaire.filtrerTaches(false));
                break;
            case "Tâches terminées":
                observableListTaches.addAll(gestionnaire.filtrerTaches(true));
                break;
            default:
                observableListTaches.addAll(gestionnaire.getTaches());
                break;
        }
        
        // Mise à jour du statut
        int totalTaches = gestionnaire.getTaches().size();
        int tachesTerminees = gestionnaire.filtrerTaches(true).size();
        int tachesEnCours = gestionnaire.filtrerTaches(false).size();
        
        labelStatut.setText(String.format("📊 Total: %d | En cours: %d | Terminées: %d", 
                                         totalTaches, tachesEnCours, tachesTerminees));
    }
    
    /**
     * Affiche une alerte d'information
     */
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        System.out.println("Démarrage de l'application avec Maven...");
        launch(args);
    }
}