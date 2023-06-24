drop table tb_chat_room cascade;

create table tb_chat_room(
   chat_room_sq       INT AUTO_INCREMENT PRIMARY KEY,
   channel_id         VARCHAR(20) NOT NULL,
   title              VARCHAR(20) NOT NULL,
   writer_nm          VARCHAR(20) NOT NULL,
   date_time          TIMESTAMP NOT NULL
);