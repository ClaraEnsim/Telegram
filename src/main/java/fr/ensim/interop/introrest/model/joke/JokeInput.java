package fr.ensim.interop.introrest.model.joke;

public class JokeInput {
    private String titre;
    private String texte;
    private Double note;

    public JokeInput() {}

    public JokeInput(String titre, String texte, Double note) {
        this.titre = titre;
        this.texte = texte;
        this.note = note;
    }

    // Getters et Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }

    public Double getNote() { return note; }
    public void setNote(Double note) { this.note = note; }
}
