CREATE DATABASE HOTEL_DB ;
GO 

USE HOTEL_DB ;
GO

CREATE TABLE SHOP_OWNER(
	owner_id INT IDENTITY(1,1) PRIMARY KEY,
	username VARCHAR(50) NOT NULL UNIQUE,
	password_hash VARCHAR(100) NOT NULL,
	salt VARCHAR(50) NOT NULL,
	full_name NVARCHAR(100) NOT NULL,
	created_at DATETIME2(0) NOT NULL DEFAULT SYSDATETIME(), -- 0 means the precision of datetime (to last second)
	last_login_at DATETIME2(0)) ;
GO 


CREATE TABLE ITEM(
	item_id INT IDENTITY(1,1) PRIMARY KEY, -- FIRST 1(Starting Value) SECOND 1(StepValue)
	name NVARCHAR(100) NOT NULL,
	price_per_unit DECIMAL(10,2) NOT NULL,
	stock_amount DECIMAL(10,2) NOT NULL DEFAULT 0, 
	measuring_method VARCHAR(10) NOT NULL DEFAULT 'COUNT', -- COUNT,CONTINUOUS
	is_active BIT NOT NULL DEFAULT 1,
	CHECK (price_per_unit >= 0),
	CONSTRAINT CHK_measuring_method CHECK(measuring_method IN('COUNT','CONTINUOUS')) ,
	CONSTRAINT CHK_item_stock CHECK (stock_amount >= 0)) ;
GO


CREATE TABLE ROOM(
	room_id INT IDENTITY(1,1) PRIMARY KEY,
	room_number VARCHAR(10) NOT NULL UNIQUE,
	room_type VARCHAR(50) NOT NULL,
	price_per_night DECIMAL(10,2) NOT NULL,
	status VARCHAR(10) NOT NULL DEFAULT 'AVAILABLE' ,
	CONSTRAINT CHK_room_status CHECK(status IN ('AVAILABLE', 'BOOKED' , 'CLOSED' ) ) ); -- AVAILABLE,BOOKED,CLOSED
GO 


CREATE TABLE CUSTOMER(
	customer_id INT IDENTITY(1,1) PRIMARY KEY,
	full_name NVARCHAR(100) NOT NULL,
	email VARCHAR(150),
	phone VARCHAR(30),
	telegram_chat_id BIGINT,
	whatsapp_number VARCHAR(30),
	times_denied INT NOT NULL DEFAULT 0,
	last_denied_date DATE,
	created_at DATETIME2(0) NOT NULL DEFAULT SYSDATETIME() );
GO


CREATE TABLE BOOKING_REQUEST(
	request_id INT IDENTITY(1,1) PRIMARY KEY,
	customer_id INT,
	email_message_id VARCHAR(100) NOT NULL UNIQUE,
	booking_ref VARCHAR(50), -- Booking.com reservation number written in the email
	guest_name NVARCHAR(100) NOT NULL,
	guest_email VARCHAR(150), -- Optional
	guest_phone VARCHAR(30), -- Optional
	check_in DATE NOT NULL,
	check_out DATE NOT NULL,
	rooms_wanted INT NOT NULL DEFAULT 1,
	total_price DECIMAL(10,2),
	status VARCHAR(10) NOT NULL DEFAULT 'PENDING' , -- PENDING, ACCEPTED, DENIED, CANCELED
	received_at DATETIME2(0) NOT NULL DEFAULT SYSDATETIME() ,
	decided_by INT,
	decided_at DATETIME2(0),
	CONSTRAINT FK_customer_booking_request FOREIGN KEY(customer_id) REFERENCES CUSTOMER(customer_id),
	CONSTRAINT FK_shop_owner FOREIGN KEY(decided_by) REFERENCES SHOP_OWNER(owner_id),
	CONSTRAINT CHK_status CHECK(status IN('PENDING' , 'ACCEPTED' , 'DENIED' , 'CANCELED')) ,
	CHECK (check_in < check_out) ) ;
GO

CREATE TABLE BOOKING(
	booking_id INT IDENTITY(1,1) PRIMARY KEY,
	request_id INT NOT NULL UNIQUE,
	customer_id INT NOT NULL,
	check_in DATE NOT NULL,
	check_out DATE NOT NULL,
	status VARCHAR(10) NOT NULL DEFAULT 'PENDING' ,
	created_at DATETIME2(0) NOT NULL DEFAULT SYSDATETIME(),
	CHECK (status IN('PENDING' , 'ACTIVE' , 'FINISHED' , 'CANCELED')) ,
	CHECK (check_in < check_out),
	CONSTRAINT FK_booking_booking_request FOREIGN KEY(request_id) REFERENCES BOOKING_REQUEST(request_id),
	CONSTRAINT FK_booking_customer_id FOREIGN KEY(customer_id) REFERENCES CUSTOMER(customer_id) ) ;
GO 


CREATE TABLE MESSAGE(
	message_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
	customer_id INT NOT NULL, -- can add CHANEL it's optinal
	content NVARCHAR(MAX) NOT NULL,
	status VARCHAR(10) NOT NULL, -- SENT, FAILED
	sent_at DATETIME2(0) NOT NULL DEFAULT SYSDATETIME(),
	CONSTRAINT CHK_message_status CHECK (status IN('SENT' , 'FAILED')),
	FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id)) ;
GO


CREATE TABLE PAYMENT(
	payment_id INT IDENTITY(1,1) PRIMARY KEY,
	booking_id INT NOT NULL,
	amount DECIMAL(10,2) NOT NULL,
	method VARCHAR(10) NOT NULL DEFAULT 'CASH',  -- CASH ,CARD , OTHER
	paid_at DATETIME2(0) NOT NULL DEFAULT SYSDATETIME(),
	CHECK (amount > 0 ),
	CONSTRAINT FK_payment_booking FOREIGN KEY(booking_id) REFERENCES BOOKING(booking_id),
	CONSTRAINT CHK_payment_method CHECK(method IN('CASH', 'CARD' , 'OTHER'))) ;
GO


CREATE TABLE ITEM_ORDER(
	order_id INT IDENTITY(1,1) PRIMARY KEY,
	booking_id INT NOT NULL,
	item_id INT NOT NULL,
	amount DECIMAL(10,2) NOT NULL,
	unit_price DECIMAL(10,2) NOT NULL,
	ordered_at DATETIME2(0) NOT NULL DEFAULT SYSDATETIME(),
	CHECK (amount > 0 ),
	CONSTRAINT FK_item_order_booking FOREIGN KEY(booking_id) REFERENCES BOOKING(booking_id),
	CONSTRAINT FK_item_order_item FOREIGN KEY(item_id) REFERENCES ITEM(item_id));
GO


CREATE TABLE BOOKING_ROOM(
	booking_id INT NOT NULL,
	room_id INT NOT NULL,
	price_per_night DECIMAL(10,2) NOT NULL,
	PRIMARY KEY(booking_id,room_id),
	CONSTRAINT FK_booking_room_booking FOREIGN KEY(booking_id) REFERENCES BOOKING(booking_id) ON DELETE CASCADE,
	CONSTRAINT FK_booking_room_room FOREIGN KEY(room_id) REFERENCES ROOM(room_id)) ;
GO 


CREATE TABLE CUSTOMER_REPORT(
	report_id INT IDENTITY(1,1) PRIMARY KEY,
	customer_id INT NOT NULL,
	booking_id INT NULL,
	note NVARCHAR(MAX) NOT NULL,
	created_at DATETIME2(0) NOT NULL DEFAULT SYSDATETIME(),
	CONSTRAINT FK_customer_report_customer FOREIGN KEY(customer_id) REFERENCES CUSTOMER(customer_id),
	CONSTRAINT FK_customer_report_booking FOREIGN KEY(booking_id) REFERENCES BOOKING(booking_id) );
GO 
