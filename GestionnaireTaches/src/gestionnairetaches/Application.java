package gestionnairetaches;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Application GUI pour le gestionnaire de tâches utilisant JavaFX
 */
public class ApplicationGUI extends Application {
    
    private GestionnaireTaches gestionnaire = new GestionnaireTaches();
    private ListView<Tache> listViewTaches;
    private ObservableList<Tache> observableListTaches;
    private Label labelStatut;
    private ComboBox<String> comboFiltre;
    
    @Override
    public void start(Stage primaryStage) {
        // Charger les tâches existantes
        gestionnaire.chargerTaches();
        
        // Configuration de la fenêtre principale
        primaryStage.setTitle("Gestionnaire de Tâches");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // Création de l'interface
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        
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
        labelStatut = new Label("Prêt");
        labelStatut.setStyle("-fx-padding: 5px; -fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7;");
        
        // Assemblage de l'interface
        root.getChildren().addAll(titre, barreOutils, zoneFiltrage, zoneListe, labelStatut);
        VBox.setVgrow(zoneListe, Priority.ALWAYS);
        
        // Création de la scène
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        
        // Gestion de la fermeture
        primaryStage.setOnCloseRequest(e -> {
            sauvegarderTaches();
        });
        
        // Mise à jour initiale
        mettreAJourListe();
        
        primaryStage.show();
    }
    
    /**
     * Crée la barre d'outils avec les boutons principaux
     */
    private HBox creerBarreOutils() {
        HBox barre = new HBox(10);
        barre.setAlignment(Pos.CENTER);
        barre.setPadding(new Insets(10));
        barre.setStyle("-fx-background-color: #34495e; -fx-background-radius: 5px;");
        
        Button btnAjouter = new Button("Ajouter Tâche");
        btnAjouter.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 15px;");
        btnAjouter.setOnAction(e -> ajouterTache());
        
        Button btnTerminer = new Button("Marquer Terminée");
        btnTerminer.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 15px;");
        btnTerminer.setOnAction(e -> marquerTerminee());
        
        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 15px;");
        btnSupprimer.setOnAction(e -> supprimerTache());
        
        Button btnSauvegarder = new Button("Sauvegarder");
        btnSauvegarder.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 15px;");
        btnSauvegarder.setOnAction(e -> sauvegarderTaches());
        
        barre.getChildren().addAll(btnAjouter, btnTerminer, btnSupprimer, btnSauvegarder);
        
        return barre;
    }
    
    /**
     * Crée la zone de filtrage
     */
    private HBox creerZoneFiltrage() {
        HBox zone = new HBox(10);
        zone.setAlignment(Pos.CENTER_LEFT);
        zone.setPadding(new Insets(5));
        
        Label labelFiltre = new Label("Filtrer:");
        labelFiltre.setStyle("-fx-font-weight: bold;");
        
        comboFiltre = new ComboBox<>();
        comboFiltre.getItems().addAll("Toutes les tâches", "Tâches en cours", "Tâches terminées");
        comboFiltre.setValue("Toutes les tâches");
        comboFiltre.setOnAction(e -> mettreAJourListe());
        
        zone.getChildren().addAll(labelFiltre, comboFiltre);
        
        return zone;
    }
    
    /**
     * Crée la zone d'affichage de la liste des tâches
     */
    private VBox creerZoneListe() {
        VBox zone = new VBox(5);
        
        Label labelListe = new Label("Liste des tâches:");
        labelListe.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        observableListTaches = FXCollections.observableArrayList();
        listViewTaches = new ListView<>(observableListTaches);
        listViewTaches.setPrefHeight(400);
        
        // Personnalisation de l'affichage des tâches
        listViewTaches.setCellFactory(listView -> new ListCell<Tache>() {
            @Override
            protected void updateItem(Tache tache, boolean empty) {
                super.updateItem(tache, empty);
                if (empty || tache == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(tache.toString());
                    if (tache.estTerminee()) {
                        setStyle("-fx-background-color: #d5f4e6; -fx-text-fill: #27ae60;");
                    } else {
                        LocalDate aujourd = LocalDate.now();
                        if (tache.getDateEcheance().isBefore(aujourd)) {
                            setStyle("-fx-background-color: #fadbd8; -fx-text-fill: #e74c3c;");
                        } else {
                            setStyle("-fx-background-color: #ebf3fd; -fx-text-fill: #2c3e50;");
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
     * Ajoute une nouvelle tâche
     */
    private void ajouterTache() {
        Dialog<Tache> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une nouvelle tâche");
        dialog.setHeaderText("Veuillez saisir les informations de la tâche:");
        
        // Boutons
        ButtonType buttonTypeOk = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);
        
        // Champs de saisie
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titreField = new TextField();
        titreField.setPromptText("Titre de la tâche");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description de la tâche");
        descriptionArea.setPrefRowCount(3);
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now().plusDays(1));
        
        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titreField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Date d'échéance:"), 0, 2);
        grid.add(datePicker, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // Validation
        Node addButton = dialog.getDialogPane().lookupButton(buttonTypeOk);
        addButton.setDisable(true);
        titreField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty());
        });
        
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
            labelStatut.setText("Tâche ajoutée: " + result.get().getTitre());
        }
    }
    
    /**
     * Marque une tâche comme terminée
     */
    private void marquerTerminee() {
        Tache tacheSelectionnee = listViewTaches.getSelectionModel().getSelectedItem();
        if (tacheSelectionnee == null) {
            afficherAlerte("Aucune sélection", "Veuillez sélectionner une tâche à terminer.");
            return;
        }
        
        if (tacheSelectionnee.estTerminee()) {
            afficherAlerte("Tâche déjà terminée", "Cette tâche est déjà marquée comme terminée.");
            return;
        }
        
        tacheSelectionnee.terminer();
        mettreAJourListe();
        labelStatut.setText("Tâche terminée: " + tacheSelectionnee.getTitre());
    }
    
    /**
     * Supprime une tâche
     */
    private void supprimerTache() {
        Tache tacheSelectionnee = listViewTaches.getSelectionModel().getSelectedItem();
        if (tacheSelectionnee == null) {
            afficherAlerte("Aucune sélection", "Veuillez sélectionner une tâche à supprimer.");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer la tâche");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer la tâche :\n" + tacheSelectionnee.getTitre());
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int index = gestionnaire.getTaches().indexOf(tacheSelectionnee);
            if (gestionnaire.supprimerTache(index)) {
                mettreAJourListe();
                labelStatut.setText("Tâche supprimée: " + tacheSelectionnee.getTitre());
            }
        }
    }
    
    /**
     * Sauvegarde les tâches
     */
    private void sauvegarderTaches() {
        if (gestionnaire.sauvegarderTaches()) {
            labelStatut.setText("Tâches sauvegardées avec succès.");
        } else {
            labelStatut.setText("Erreur lors de la sauvegarde des tâches.");
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
        
        labelStatut.setText(String.format("Total: %d tâches | En cours: %d | Terminées: %d", 
                                         totalTaches, tachesEnCours, tachesTerminees));
    }
    
    /**
     * Affiche une alerte d'information
     */
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}