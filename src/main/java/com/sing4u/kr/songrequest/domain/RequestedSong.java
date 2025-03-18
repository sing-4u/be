package com.sing4u.kr.songrequest.domain;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Access(AccessType.FIELD)
@Table(name = "requested_song")
public class RequestedSong {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
