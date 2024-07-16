package synergyhubback.calendar.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import synergyhubback.calendar.domain.entity.Label;
import synergyhubback.calendar.dto.request.LabelCreateRequest;
import synergyhubback.calendar.dto.request.LabelUpdateRequest;
import synergyhubback.calendar.dto.response.LabelResponse;
import synergyhubback.calendar.service.LabelService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @PostMapping("/label/regist")
    public LabelResponse createLabel(@RequestBody LabelCreateRequest labelRequest) {
        Label label = new Label();
        label.setLabelTitle(labelRequest.getLabelTitle());
        label.setLabelCon(labelRequest.getLabelCon());
        label.setLabelColor(labelRequest.getLabelColor());

        Label savedLabel = labelService.createLabel(label);
        return LabelResponse.from(savedLabel);
    }

    @PutMapping("/label/{labelId}")
    public LabelResponse updateLabel(@PathVariable Long labelId, @RequestBody LabelUpdateRequest labelRequest) {
        Label label = labelService.findById(labelId).orElseThrow(() -> new RuntimeException("Label not found"));
        label.setLabelTitle(labelRequest.getLabelTitle());
        label.setLabelCon(labelRequest.getLabelCon());
        label.setLabelColor(labelRequest.getLabelColor());

        Label updatedLabel = labelService.updateLabel(label);
        return LabelResponse.from(updatedLabel);
    }

    @GetMapping("/labels")
    public List<LabelResponse> getAllLabels() {
        return labelService.findAll().stream().map(LabelResponse::from).collect(Collectors.toList());
    }

    @GetMapping("/label/{labelId}")
    public LabelResponse getLabelById(@PathVariable Long labelId) {
        Label label = labelService.findById(labelId).orElseThrow(() -> new RuntimeException("Label not found"));
        return LabelResponse.from(label);
    }

    @DeleteMapping("/label/{labelId}")
    public void deleteLabel(@PathVariable Long labelId) {
        labelService.deleteById(labelId);
    }
}
