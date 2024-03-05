package com.fithub.fithubbackend.domain.chat.application;

import com.fithub.fithubbackend.domain.chat.domain.Chat;
import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;
import com.fithub.fithubbackend.domain.chat.dto.ChatRequestDto;
import com.fithub.fithubbackend.domain.chat.dto.ChatRoomResponseDto;
import com.fithub.fithubbackend.domain.chat.repository.ChatRepository;
import com.fithub.fithubbackend.domain.chat.repository.ChatRoomRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ChatRoomResponseDto findById(final Long id) {
        ChatRoom entity = this.chatRoomRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND, "채팅룸이 존재하지 않음"));
        return new ChatRoomResponseDto(entity);
    }

    @Transactional
    @Override
    public List<ChatRoomResponseDto> findChatRoomDesc(User user) {
        // Chat 테이블: 현재 유저의 채팅방 id와 채팅방 이름 가져옴
        List<Chat> chatList = this.chatRepository.findChatsByUserId(user.getId());

        // ChatRoom 테이블: 위에서 가져온 채팅방의 추가 정보를 가져옴
        List<ChatRoomResponseDto> dtoList = new ArrayList<>();
        Iterator<Chat> chatIterator = chatList.iterator();

        while(chatIterator.hasNext()) {
            Chat chat = chatIterator.next();
            ChatRoomResponseDto dto = new ChatRoomResponseDto(chat);
            dto.setModifiedDate(this.chatRoomRepository.findByRoomId(dto.getRoomId()).getModifiedDate());
            dtoList.add(dto);
        }

        // modifiedDate 기준으로 정렬
        Comparator<ChatRoomResponseDto> comparator = Comparator.comparing(ChatRoomResponseDto::getModifiedDate);
        Collections.sort(dtoList, comparator);
        return dtoList;
    }

    @Transactional
    @Override
    public Long save(User user, String receiverNickname) {
        // chatRoom 테이블에 저장
        Long roomId = this.chatRoomRepository.save(new ChatRoom()).getRoomId();

        // chatRequestDto 생성 후 chat 테이블에 저장 (chatRoom과 user의 연관관계)
        Optional<ChatRoom> chatRoomOptional = this.chatRoomRepository.findById(roomId);
        ChatRoom chatRoom = chatRoomOptional.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "채팅룸이 존재하지 않음"));

        // 채팅룸ID-본인ID
        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .chatRoomName(receiverNickname)
                .user(user)
                .build();
        this.chatRepository.save(chat);

        // 채팅룸ID-상대ID


        Optional<User> receiverUserOptional = userRepository.findByNickname(receiverNickname);
        chat = Chat.builder()
                .chatRoom(chatRoom)
                .chatRoomName("새로운 채팅")
                .user(receiverUserOptional.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "상대 유저가 존재하지 않음")))
                .build();
        this.chatRepository.save(chat);

        return roomId;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        ChatRoom entity = this.chatRoomRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND, "채팅룸이 존재하지 않음"));
        this.chatRoomRepository.delete(entity);
    }
}
