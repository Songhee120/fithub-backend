package com.fithub.fithubbackend.domain.chat.repository;

import com.fithub.fithubbackend.domain.chat.domain.ChatMessage;
import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoomOrderByCreatedDateDesc(ChatRoom chatRoom);

    @Modifying
    @Query("UPDATE ChatMessage c SET c.checked = true WHERE c.chatRoom.roomId = :roomId")
    void updateCheckedByRoomId(Long roomId);
}
