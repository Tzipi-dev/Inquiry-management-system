package ClientServer;

import Data.Inquiry;
import Data.Manager;
import Data.Representative;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class HandleClient extends Thread {

    Socket clientSocket;

    public HandleClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        handleClientRequest();
    }

    public void handleClientRequest() {
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            boolean running = true;
            while (running) {
                RequestData requestData = (RequestData) in.readObject();
                InquiryManagerActions action = requestData.getAction();
                System.out.println(action);
                switch (action) {
                    case ADD_INQUIRY -> {
                        List<Inquiry> inquiries = requestData.castParameters(Inquiry.class);
                        for (Inquiry inquiry : inquiries) {
//                            InquiryManager.getInstance().addInquiry(inquiry);
                            Manager.getInstance().addInquiry(inquiry);
                        }
                        System.out.println("reading :---" + inquiries.getFirst().getData() + " last :" + inquiries.getLast().getData());
                        out.writeObject(new ResponseData(ResponseStatus.SCCESS, "Inquiries added successfully"));
                    }
                    case DELETE_REPRESENTATIVE_BY_ID -> {
                        List<Integer> ids = requestData.castParameters(Integer.class);
                        for (Integer id : ids) {
                            Manager.getInstance().deleteRepresentative(id);
                            System.out.println("Deleted representative with ID: " + id);
                        }
                        out.writeObject(new ResponseData(ResponseStatus.SCCESS, "Representatives deleted successfully"));
                    }
                    case ADD_REPRESENTATIVE -> {
                        List<Representative> reps = requestData.castParameters(Representative.class);
                        for (Representative rep : reps) {
                            Manager.getInstance().addRepresentative(rep);
                            System.out.println("Added representative: " + rep.getName());
                        }
                        out.writeObject(new ResponseData(ResponseStatus.SCCESS, "Representatives added successfully"));
                    }
                    case ALL_INQUIRY -> {
                        LinkedList<Inquiry> allInquiries = (LinkedList<Inquiry>) Manager.getInstance().getAllInquiriesQueue();
                        out.writeObject(new ResponseData(ResponseStatus.SCCESS,"your requst succeded",allInquiries));
                    }
                    case CANCELLATION_INQUIRY -> {
                        List<Integer> ids = requestData.castParameters(Integer.class);
                        for (Integer id : ids) {
                            Manager.getInstance().deleteRepresentative(id);
                            System.out.println("Deleted representative with ID: " + id);
                        }
                        out.writeObject(new ResponseData(ResponseStatus.SCCESS, "Representatives deleted successfully"));
                    }

                        case EXIT -> {
                        out.writeObject(new ResponseData(ResponseStatus.SCCESS, "Connection closing"));
                        running = false; // יציאה מהלולאה
                    }
                    default -> throw new IllegalArgumentException("Unknown action: " + action);
                }
                out.flush();
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            throw new RuntimeException("Error handling client request", e);
        }

    }
}
