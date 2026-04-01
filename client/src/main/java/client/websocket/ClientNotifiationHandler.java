package client.websocket;

import chess.ChessGame;
import websocket.messages.ServerMessage;
import ui.BoardPrinter;
import client.Client.loadGame;

public class ClientNotifiationHandler {
    public void notify(ServerMessage notification) {
        switch (notification.getServerMessageType()) {
            case LOAD_GAME -> loadGame(notification);
            case ERROR -> printError(notification);
            case NOTIFICATION -> printMessage(notification);
            case ENTER -> enter(notification);
        }
    }

    public void loadGame(ServerMessage msg) {
        ChessGame game = msg.getGame();

        BoardPrinter.printChessBoard(game.getBoard(), msg.getColor());
        System.out.println(msg.getMsg());
    }

    public void printError(ServerMessage msg) {
        System.out.println("Error: " + msg.getMsg());
    }

    public void printMessage(ServerMessage msg) {
        System.out.println(msg.getMsg());
    }

    public void enter(ServerMessage msg) {
        System.out.println(msg.getMsg());
    }
}
