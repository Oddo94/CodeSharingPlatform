package platform.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import platform.exception.NoDataFound;
import platform.model.Data;
import platform.service.CodeSharingService;
import platform.utils.DetailedResponse;
import platform.utils.EmptyJson;
import platform.utils.UploadResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CodeSharingController {
    @Autowired
    private CodeSharingService codeSharingService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeSharingController.class);

    @RequestMapping(value="api/code/{UUID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity getCodeSnippetById(@PathVariable String UUID) {

        Data codeSnippet = codeSharingService.getNthCodeSnippet(UUID);

        LOGGER.info("api/code/{UUID}-CHECKING IF RETRIEVED OBJECT IS NULL...");
        if (codeSnippet == null) {
            LOGGER.info(String.format("api/code/{UUID}-COULD NOT FIND UUID: %s", UUID));
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        LOGGER.info(String.format("api/code/{UUID}-DATA OBJECT \n CODE: %s \n CREATION DATE: %s \n EXPIRY DATE %s \n TIME: %s \n VIEWS: %s \n UUID:%s", codeSnippet.getCode(), codeSnippet.getDate(), codeSnippet.getExpiryDate(), codeSnippet.getTime(), codeSnippet.getViews(), codeSnippet.getUUID()));


        return new ResponseEntity(codeSnippet, HttpStatus.OK);
    }

    @RequestMapping(value="/code/{UUID}", method = RequestMethod.GET)
    public String codeResponsePage(@PathVariable String UUID, Model model) throws NoDataFound {
        Data codeSnippet = codeSharingService.getNthCodeSnippet(UUID);

        LOGGER.info("/code/{UUID}-CHECKING IF RETRIEVED OBJECT IS NULL...");
        if (codeSnippet == null) {
            LOGGER.info(String.format("/code/{UUID}-COULD NOT FIND UUID: %s", UUID));
           String message = String.format("The code snippet having UUID %s could not be found.", UUID);

            throw new NoDataFound(message);
        }

        LOGGER.info(String.format("/code/{UUID}-DATA OBJECT \n CODE : %s \n CREATION DATE: %s \n EXPIRY DATE %s \n TIME: %s \n VIEWS: %s \n UUID:%s", codeSnippet.getCode(), codeSnippet.getDate(), codeSnippet.getExpiryDate(), codeSnippet.getTime(), codeSnippet.getViews(), codeSnippet.getUUID()));
        model.addAttribute("creationDate", codeSnippet.getDate());
        model.addAttribute("resultCodeSnippet", codeSnippet.getCode());
        model.addAttribute("time", codeSnippet.getTime());
        model.addAttribute("views", codeSnippet.getViews());

        //CHANGE!!
        if (codeSnippet.isHasInitialViewsRestriction() && codeSnippet.getViews() == 0) {

            return "special_response";
        }

        return "response";
    }

    @RequestMapping(value ="/api/code/new", method = RequestMethod.POST)
    public ResponseEntity postAPICode(@RequestBody Data data) {
        String codeSnippetUUID = codeSharingService.addCode(data);

        LOGGER.info(String.format("/api/code/new-DATA OBJECT \n CODE : %s \n CREATION DATE: %s \n TIME: %s \n VIEWS:%s \n UUID:%s", data.getCode(), data.getDate(), data.getTime(), data.getViews(), data.getUUID()));
        UploadResponse uploadResponse = new UploadResponse(codeSnippetUUID);

        return new ResponseEntity(uploadResponse, HttpStatus.OK);

    }

    @RequestMapping(value ="code/new",  method = RequestMethod.GET)
    public String codeForm(Model model) {
        LOGGER.info("code/new");

        return "input_form";
    }

    @RequestMapping(value="api/code/latest", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Data> getLatestCodeSnippets() {
        LOGGER.info("api/code/latest");
        List<Data> codeSnippetList = codeSharingService.getFirstTenCodeSnippets();

        LOGGER.info(String.format("api/code/latest-FIRST CODE SNIPPET OBJECT:\n CODE:%s \n CREATION DATE:%s", codeSnippetList.get(0).getCode(), codeSnippetList.get(0).getDate()));

        return codeSnippetList;
    }

    @RequestMapping(value="code/latest", method=RequestMethod.GET)
    public String getLatestCodeSnippetsPage(Model model) {
        LOGGER.info("code/latest");

        List<Data> codeSnippetList = codeSharingService.getFirstTenCodeSnippets();
        LOGGER.info(String.format("code/latest-FIRST CODE SNIPPET OBJECT: \n CODE:%s \n CREATION DATE:%s", codeSnippetList.get(0).getCode(), codeSnippetList.get(0).getDate()));

        model.addAttribute("codeSnippets", codeSnippetList);

        return "latest_snippets";
    }
}