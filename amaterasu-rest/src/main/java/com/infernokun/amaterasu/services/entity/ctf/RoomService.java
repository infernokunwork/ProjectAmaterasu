package com.infernokun.amaterasu.services.entity.ctf;

import com.infernokun.amaterasu.exceptions.ResourceNotFoundException;
import com.infernokun.amaterasu.models.entities.ctf.Room;
import com.infernokun.amaterasu.repositories.ctf.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    public Room findByRoomName(String roomName) {
        return roomRepository.findByName(roomName)
                .orElseThrow(() -> new ResourceNotFoundException("Room with name '" + roomName + "' not found."));
    }

    public Room findByRoomId(String id) {
        return roomRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Room with id '" + id + "' not found."));
    }

    public List<Room> findAllByIds(List<String> ids) {
        return roomRepository.findAllByIdIn(ids);
    }

    public Room saveRoom(Room room) { return roomRepository.save(room); }

    public List<Room> saveRooms(List<Room> rooms) {
        return roomRepository.saveAll(rooms);
    }

    public void deleteRoom(String id) {
        roomRepository.deleteById(id);
    }
}
