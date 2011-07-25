package httpTTT;

import com.sun.xml.internal.fastinfoset.algorithm.BooleanEncodingAlgorithm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server implements Runnable {

  ServerSocket serverSocket;
  private int connections = 0;
  private ConnectionServer connectionServer;
  private boolean running = false;

  public Server(int _port, ConnectionServer connectionServer) throws Exception {
    this.connectionServer = connectionServer;
    try {
      serverSocket = new ServerSocket(_port);
    } catch (IOException e) {
      System.out.println("Could not listen to port: " + _port);
      System.exit(-1);
    }
  }

  public void closeServerSocket() {
    try {
      running = false;
      serverSocket.close();
    } catch (IOException e) {
      System.out.println("Could not close connection to port: " + serverSocket.getLocalPort());
    }
  }

  public void start() {
    new Thread(this).start();
  }

  public void run() {
    try {
      running = true;
      while (running) {
        Socket clientSocket = serverSocket.accept();
        serveConnection(clientSocket);
        connections++;
      }
    } catch (SocketException e) {
      waitForClose();
    } catch (Exception e) {
      System.out.println("Could not connect to server at port: " + serverSocket.getLocalPort());
      System.exit(-1);
    }
  }

  private void serveConnection(Socket clientSocket) throws Exception {
    new Thread(new ConnectionServerDriver(clientSocket)).start();
  }

  private void waitForClose() {
    try {
      Thread.sleep(10); // let close finish
    } catch (InterruptedException e1) {

    }
  }

  public int getConnectionCount() {
    return connections;
  }

  private class ConnectionServerDriver implements Runnable {
    private Socket clientSocket;

    public ConnectionServerDriver(Socket clientSocket) {
      this.clientSocket = clientSocket;
    }
    public void run() {
      try {
        connectionServer.serve(clientSocket);
        clientSocket.close();
      } catch (Exception e) {
        System.out.println("HORRORS!");
      }

    }
  }
}
