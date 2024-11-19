package org.zir.dragonieze;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dragon.Location;
import org.zir.dragonieze.dragon.repo.LocationRepository;
import org.zir.dragonieze.dto.LocationDTO;
import org.zir.dragonieze.services.BaseService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/user/loc")
public class LocationController extends Controller {
    private final LocationRepository locationRepository;

    public LocationController(BaseService service, LocationRepository locationRepository) {
        super(service);
        this.locationRepository = locationRepository;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addLocation(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Location location
    ) throws JsonProcessingException {
        Location savedLocation = service.saveEntityWithUser(header, location, Location::setUser, locationRepository);
        String json = service.convertToJson(new LocationDTO(savedLocation));
        return ResponseEntity.ok(json);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCoordinates(
            @RequestHeader(HEADER_AUTH) String header,
            @PathVariable Long id
    ) throws JsonProcessingException {
        service.deleteEntityWithCondition(
                header,
                id,
                Location::getUser,
                locationRepository
        );
        return ResponseEntity.ok("удалилось ура");
    }

//    @GetMapping("/get")
//    public ResponseEntity<String> getLocations(
//            @RequestHeader(HEADER_AUTH) String header
//    ) throws JsonProcessingException {
//        String username = getUsername(header, jwtUtil);
//        Optional<User> userOptional = userRepository.findByUsername(username);
//
//        if (!userOptional.isPresent()) {
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//        }
//        User user = userOptional.get();
//        List<Location> locations = locationRepository.findByUserId(user.getId());
//        List<LocationDTO> locationDTOS = locations.stream()
//                .map(LocationDTO::new)
//                .toList();
//        String json = convertToJson(locationDTOS);
//        return ResponseEntity.ok(json);
//    }

    @Transactional
    @PostMapping("/update")
    public ResponseEntity<String> updateLocation(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Location location
    ) throws JsonProcessingException {
        Location updateLocation = service.updateEntityWithUser(
                header,
                location,
                location.getId(),
                locationRepository::findById,
                Location::getUser,
                (old, updated) -> {
                    old.setX(updated.getX());
                    old.setY(updated.getY());
                    old.setCanEdit(updated.getCanEdit());
                },
                locationRepository
        );
        String json = service.convertToJson(new LocationDTO(updateLocation));
        return ResponseEntity.ok(json);
    }


}
