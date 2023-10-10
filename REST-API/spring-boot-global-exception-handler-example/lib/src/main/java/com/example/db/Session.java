package com.example.db;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob; // Import the @Lob annotation
import java.time.LocalDateTime;

@Entity
@Data
public class Session {
	@Id
	private long session_id; // Primary key
	private long user_id;
	private LocalDateTime session_start;
	private LocalDateTime session_end;
	private LocalDateTime created_timestamp;
	private LocalDateTime last_updated_timestamp;
	private String status;

	@Lob // Add this annotation to represent a BLOB field
	private byte[] session_data; // Assuming the session data is stored as a byte array
}
