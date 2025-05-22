package fr.ensim.interop.introrest.model.joke;

public class Joke {
    private Long id;
    private String titre;
    private String texte;
    private Double note;

    public Joke() {}

    public Joke(Long id, String titre, String texte, Double note) {
        this.id = id;
        this.titre = titre;
        this.texte = texte;
        this.note = note;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }

    public Double getNote() { return note; }
    public void setNote(Double note) { this.note = note; }
}
