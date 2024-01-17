package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Classe de commentaire d'un {@link Sondage}
 */
@Entity
@Table(name = "commentaire")
public class Commentaire {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commentaire that = (Commentaire) o;
        return Objects.equals(commentaireId, that.commentaireId) && Objects.equals(commentaire, that.commentaire) && Objects.equals(sondage, that.sondage) && Objects.equals(participant, that.participant);
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "commentaireId=" + commentaireId +
                ", commentaire='" + commentaire + '\'' +
                ", sondage=" + sondage +
                ", participant=" + participant +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentaireId, commentaire, sondage, participant);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentaire_id")
    private Long commentaireId;

    @Column(name = "commentaire")
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sondage_id")
    private Sondage sondage = new Sondage();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private Participant participant = new Participant();

    public Commentaire() {}

    public Long getCommentaireId() {
        return commentaireId;
    }

    public void setCommentaireId(Long commentaireId) {
        this.commentaireId = commentaireId;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Sondage getSondage() {
        return sondage;
    }

    public void setSondage(Sondage sondage) {
        this.sondage = sondage;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
}
