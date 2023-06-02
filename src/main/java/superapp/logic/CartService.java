package superapp.logic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.converters.ObjectConverter;
import superapp.dal.ObjectCrud;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import superapp.utils.exceptions.NotFoundException;
import java.io.IOException;

@Service("Cart")
public class CartService implements MiniAppService {
    private final ObjectCrud objectCrud;
    private final ObjectConverter objectConverter;

    private final Log logger = LogFactory.getLog(CartService.class);

    @Autowired
    public CartService(ObjectCrud objectCrud, ObjectConverter objectConverter) {
        this.objectCrud = objectCrud;
        this.objectConverter = objectConverter;
    }

    @Override
    public Object runCommand(MiniAppCommandBoundary command) {
        this.logger.debug("Running command in Cart MiniApp: " + command.getCommand());
        String commandOpt = command.getCommand();
        if (commandOpt.equals("removeProducts")) {
            this.removeProduct(command);
        } else {
            this.logger.error("Undefined Command in Cart MiniApp ");
            throw new NotFoundException("Undefined Command");
        }
        this.logger.trace("Run command in Cart MiniApp Succeed");
        return null;
    }

    public void removeProduct(MiniAppCommandBoundary command) {
        this.logger.debug("Remove product from Cart MiniApp: " + command.getCommand());
        String productId = this.objectConverter.createID(command.getTargetObject().getObjectId());
        if (this.objectCrud.findById(productId).isEmpty()) {
            this.logger.error("Couldn't Find Product {%s}".formatted(productId));
            throw new NotFoundException("Couldn't Find Product {%s}".formatted(productId));
        }
        this.objectCrud.deleteById(productId);
        this.logger.trace("product Removed from Cart MiniApp");

    }

    public void createPdfFromJson(String jsonData, String outputPath) {
        this.logger.debug("Create pdf from json product from Cart MiniApp");
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Set font and font size
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

            // Write JSON data to the PDF
            contentStream.beginText();
            contentStream.newLineAtOffset(25, 700);
            contentStream.showText(jsonData);
            contentStream.endText();

            contentStream.close();

            // Save the PDF
            document.save(outputPath);
            this.logger.trace("Created pdf from json product from Cart MiniApp");
        } catch (IOException e) {
            this.logger.fatal("Error in Create pdf from json: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
