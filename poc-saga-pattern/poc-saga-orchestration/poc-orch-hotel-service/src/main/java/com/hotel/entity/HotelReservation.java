package com.hotel.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_reservation")
@Data
public class HotelReservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "hotel_id")
  private Long hotelId;

  @Column(name = "room_id")
  private Long roomId;

  @Column(name = "client_id")
  private Long clientId;

  @Column(name = "reservation_start_date")
  private LocalDateTime reservationStartDate;

  @Column(name = "reservation_end_date")
  private LocalDateTime reservationEndDate;
}
