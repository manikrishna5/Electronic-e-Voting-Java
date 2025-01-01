import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;  
import javax.swing.*;

class Voter {
    private String voterId;
    private String name;
    private String constituency;
    private boolean hasVoted;

    public Voter(String voterId,String name, String constituency, boolean hasVoted) {
        this.voterId = voterId;
        this.name = name;
        this.constituency = constituency;
        this.hasVoted = hasVoted;
    }

    public boolean hasVoted() {
        return hasVoted;
    }
    public String getVoterId(){
        return voterId;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public String getConstituency() {
        return constituency;
    }

    @Override
    public String toString() {
        return "Name:\t" + name + "\n" +
               "Constituency:\t" + constituency + "\n" +
               "Has Voted:\t" + hasVoted + "\n";
    }
}

class Candidate {
    private String name;
    private String constituency;
    private String party;
    private String education;
    private int voteCount;
    private String ECid;

    public Candidate(String name, String constituency, String party, String education,int voteCount,String ECid) {
        this.name = name;
        this.constituency = constituency;
        this.party = party;
        this.education = education;
        this.voteCount = voteCount;
        this.ECid = ECid;
    }

    public String getConstituency() {
        return constituency;
    }

    public String getName() {
        return name;
    }

    public void incrementVoteCount() {
        voteCount++;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public String getCandidateDetailsForVoters() {
        return "Name:\t" + name + "\n" +
               "Party:\t" + party + "\n" +
               "Education:\t" + education + "\n";
    }

    public String getwinnerData() {
        return "Name:\t" + name + "\n" +
                "Party:\t" + party + "\n" +
                "Education:\t" + education + "\n" +
                "No.of Votes:\t" + voteCount + "\n";
    }

}



class VoterLoad {
    String fileName = "C:\\JavaLabProject\\voters.txt";
    Map<String, Voter> voterMap = new HashMap<>();

    public void loadVoters() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String voterId = parts[0];
                    String name = parts[1];
                    String constituency = parts[2];
                    boolean hasVoted = Boolean.parseBoolean(parts[3]);

                    Voter voter = new Voter(voterId,name, constituency, hasVoted);
                    voterMap.put(voterId, voter);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class CandidateLoad {
    String fileName = "C:\\JavaLabProject\\candidate.txt";
    Map<String, List<Candidate>> candidateMap = new HashMap<>();

    public void loadCandidates() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String constituency = parts[0];
                    String name = parts[1];
                    String party = parts[2];
                    String education = parts[3];
                    int voteCount = Integer.parseInt(parts[4]);
                    String ECid = parts[5];

                    Candidate candidate = new Candidate(name, constituency, party, education,voteCount,ECid);
                    candidateMap.computeIfAbsent(constituency, k -> new ArrayList<>()).add(candidate);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Map<String, List<Candidate>> getCandidateMap() {
        return candidateMap;
    }
    public boolean updateCandidateFile(String candidateName) {
        boolean updateSuccess = false;
        File originalFile = new File(fileName);
        File tempFile = new File(fileName + ".tmp");

        try (BufferedReader br = new BufferedReader(new FileReader(originalFile));
             PrintWriter pw = new PrintWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6 && parts[1].equals(candidateName)) {
                    // Increment the vote count
                    int currentVotes = Integer.parseInt(parts[4]);
                    parts[4] = String.valueOf(currentVotes + 1);
                    pw.println(String.join(",", parts)); // Write updated line
                } else {
                    pw.println(line); // Write unchanged lines
                }
            }

            // Close the resources before file manipulation
            br.close();
            pw.close();

            // Replace the original file with the updated temp file
            if (originalFile.delete() && tempFile.renameTo(originalFile)) {
                updateSuccess = true; // File update successful
            } else {
                System.err.println("Failed to replace the original candidate file.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return updateSuccess;
    }
    
}

class VoterValidationUI extends JFrame {
    private VoterLoad voterLoad;
    private JTextField voterIdField;
    private JButton validateButton;
    private JLabel messageLabel;
    private JLabel adminmsglabel;
    private JButton voteButton;
    private JPanel voterDetailsPanel;
    private JLabel voterDetailsLabel;

    public VoterValidationUI(VoterLoad voterLoad) {
        this.voterLoad = voterLoad;
        initializeUI();
    }

    public VoterValidationUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Voter Validation");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel headPanel = new JPanel();
        headPanel.setLayout(new GridLayout());
        JLabel headLabel = new JLabel("Electronic Voting Machine (e-voting)");
        headLabel.setForeground(Color.WHITE);
        headLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headPanel.add(headLabel);
        headPanel.setBackground(Color.getHSBColor(0.0f, 0.0f, 0.129f));
        add(headPanel);

        add(Box.createVerticalStrut(40));
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel voterIdLabel = new JLabel("Enter Voter ID:");
        voterIdField = new JTextField(15);
        inputPanel.add(voterIdLabel);
        inputPanel.add(voterIdField);
        detailPanel.add(inputPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        validateButton = new JButton("Validate");
        buttonPanel.add(validateButton);
        detailPanel.add(buttonPanel);

        JPanel footpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messageLabel = new JLabel("");
        footpanel.add(messageLabel);
        detailPanel.add(footpanel);

        JPanel Adminpanel = new JPanel();
        Adminpanel.setLayout(new BoxLayout(Adminpanel, BoxLayout.Y_AXIS));

        JPanel adminpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel adminlabel = new JLabel("Enter Election Comission ID: ");
        adminlabel.setForeground(Color.WHITE);
        JTextField admintext = new JTextField(10);
        adminpanel.add(adminlabel);
        adminpanel.add(admintext);
        adminpanel.setBackground(Color.getHSBColor(0.0f, 0.0f, 0.129f));

        JPanel bpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton adminButton = new JButton("Enter");
        bpanel.add(adminButton);
        bpanel.setBackground(Color.getHSBColor(0.0f, 0.0f, 0.129f));

        adminmsglabel = new JLabel("");

        Adminpanel.add(adminpanel);
        Adminpanel.add(bpanel);
        Adminpanel.add(adminmsglabel);
        detailPanel.add(Adminpanel);

        add(detailPanel);
        setVisible(true);

        admintext.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String input1 = admintext.getText();
                input1 = input1.toUpperCase();
                admintext.setText(input1);
                if (input1.matches("[A-Z]{3}\\d{3}")) {
                    adminmsglabel.setText(""); // Clear error message if valid
                } else {
                    adminmsglabel.setText("Invalid Format");
                }
            }
        });

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = admintext.getText().trim();
                String Const = " ";
                if(id.equals("ECB123")){
                    Const = "Banjara Hills";
                } else if(id.equals("ECJ123")){
                    Const = "Jubliee Hills";
                } else if(id.equals("ECB456")){
                    Const = "Bachupally";
                } else if(id.equals("ECL123")){
                    Const = "Lakdi-ka-Pool";
                } else if(id.equals("ECK123")){
                    Const = "Khairathabad";
                } else if(id.equals("ECS123")){
                    Const = "Secunderabad";
                }
                CandidateLoad candidateLoad = new CandidateLoad();
                candidateLoad.loadCandidates();
                List<Candidate> candidates = candidateLoad.candidateMap.get(Const);
                if (candidates != null) {
                    Voter voter = null;
                    new CandidateWinnerPage(Const,candidates);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid EC ID");
                    admintext.setText("");
                }
                
 
            }
        });

        voterIdField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String input = voterIdField.getText();
                input = input.toUpperCase();
                voterIdField.setText(input);
                if (input.matches("[A-Z]{3}\\d{3}")) {
                    messageLabel.setText(""); // Clear error message if valid
                } else {
                    messageLabel.setText("Enter Valid Voter ID : YYY999");
                }
            }
        });

        validateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String voterId = voterIdField.getText().trim();
                VoterLoad ob = new VoterLoad();
                ob.loadVoters();

                if (validateVoter(voterId, ob)) {
                    Voter voter = ob.voterMap.get(voterId);
                    if (!voter.hasVoted()) {
                        new VoterDetailsPage(voter); // Open the new page
                        dispose(); // Close the current validation UI
                    } else {
                        messageLabel.setText("Voter already voted.");
                    }
                } else {
                    messageLabel.setText("Invalid voter ID");
                }
            }
        });
    }

    private boolean validateVoter(String voterId, VoterLoad ob) {
        return ob.voterMap.containsKey(voterId);
    }
}
class CandidateWinnerPage extends JFrame{
    public String Const;
    private List<Candidate> candidates;
    public CandidateWinnerPage(String Const, List<Candidate> candidates){
        this.Const = Const;
        setTitle("Candidates for " + Const);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        for (Candidate candidate : candidates) {
            JPanel candidatePanel = new JPanel();
            candidatePanel.setLayout(new BoxLayout(candidatePanel, BoxLayout.X_AXIS));

            JLabel candidateLabel = new JLabel("<html>" + candidate.getwinnerData().replace("\n", "<br>") + "</html>");
            candidatePanel.add(Box.createHorizontalStrut(10));
            candidatePanel.add(candidateLabel);
            candidatePanel.add(Box.createHorizontalStrut(10));
            candidateLabel.setAlignmentX(CENTER_ALIGNMENT);
            add(candidatePanel);
        }
        JButton returnb = new JButton("Return");
        add(returnb);

    returnb.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new VoterValidationUI(); // Open VoterValidationUI
            dispose(); // Close the current window
        }
    });
         setVisible(true);
    }
    
}

class VoterDetailsPage extends JFrame {
    public VoterDetailsPage(Voter voter) {
        setTitle("Voter Details");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding
        gbc.anchor = GridBagConstraints.CENTER; // Center alignment
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel voterDetailsLabel = new JLabel("<html>" + voter.toString().replace("\n", "<br>") + "</html>");
        add(voterDetailsLabel, gbc);

        gbc.gridy = 1;
    
        gbc.gridy = 2; // Move to the next row
        JButton voteButton = new JButton("Vote");
        add(voteButton, gbc);

        gbc.gridy = 3;

        gbc.gridy = 4;
        JLabel label = new JLabel("\"EveryVote Counts\"");
        add(label,gbc);

        setVisible(true);

        // Add action listener for the "Vote" button
        voteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CandidateLoad candidateLoad = new CandidateLoad();
                candidateLoad.loadCandidates();
                List<Candidate> candidates = candidateLoad.candidateMap.get(voter.getConstituency());

                if (candidates != null) {
                    new CandidateDisplayPage(voter, candidates);
                    //return;
                    //dispose(); // Close the voter details page
                } else {
                    JOptionPane.showMessageDialog(null, "No candidates found for your constituency.");
                    dispose();
                }
            }
        });
    }
}



class CandidateDisplayPage extends JFrame {
    private Voter voter;
    private List<Candidate> candidates;
    private Map<String, Candidate> candidateMap; // Map to track candidates by name for vote counting
    private ButtonGroup buttonGroup;
    public CandidateDisplayPage(Voter voter, List<Candidate> candidates) {
        this.voter = voter;
        this.candidates = candidates;
        this.candidateMap = new HashMap<>();
        this.buttonGroup = new ButtonGroup();

        setTitle("Candidates for " + voter.getConstituency());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        for (Candidate candidate : candidates) {
            JPanel candidatePanel = new JPanel();
            candidatePanel.setLayout(new BoxLayout(candidatePanel, BoxLayout.X_AXIS));

            JLabel candidateLabel = new JLabel("<html>" + candidate.getCandidateDetailsForVoters().replace("\n", "<br>") + "</html>");
             JRadioButton voteButton = new JRadioButton("Vote for " + candidate.getName());
            buttonGroup.add(voteButton);
            candidatePanel.add(Box.createHorizontalStrut(10));
            candidatePanel.add(candidateLabel);
            candidatePanel.add(Box.createHorizontalStrut(10));
            candidatePanel.add(voteButton);
            candidateLabel.setAlignmentX(CENTER_ALIGNMENT);
            add(candidatePanel);
            candidateMap.put(candidate.getName(), candidate);
        }
        JButton castVoteButton = new JButton("Cast Vote");
        castVoteButton.setAlignmentX(CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(20));
        add(castVoteButton);

        castVoteButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Find selected candidate
        String selectedCandidateName = null;
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements(); ) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.isSelected()) {
                selectedCandidateName = button.getText().replace("Vote for ", "").trim();
                break;
            }
        }

        if (selectedCandidateName != null) {
            Candidate selectedCandidate = candidateMap.get(selectedCandidateName);
            if (selectedCandidate != null) {
                selectedCandidate.incrementVoteCount();
                voter.setHasVoted(true); // Mark voter as voted

                // Update voter file
                boolean voterUpdateSuccess = updateVoterFile(voter);

                // Update candidate file
                CandidateLoad candidateLoad = new CandidateLoad();
                boolean candidateUpdateSuccess = candidateLoad.updateCandidateFile(selectedCandidateName);

                if (voterUpdateSuccess && candidateUpdateSuccess) {
                    JOptionPane.showMessageDialog(CandidateDisplayPage.this, "Vote recorded. Thank you for voting!");
                    new VoterValidationUI(); // Return to validation UI
                    dispose(); // Close the candidate display page
                } else {
                    JOptionPane.showMessageDialog(CandidateDisplayPage.this, "Failed to record vote. Please try again.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(CandidateDisplayPage.this, "Please select a candidate before casting your vote.");
        }
    }
});

        setVisible(true);
    }
    private boolean updateVoterFile(Voter voter) {
    String voterFilePath = "C:\\JavaLabProject\\voters.txt";
    boolean updateSuccess = false;

    File originalFile = new File(voterFilePath);
    File tempFile = new File(voterFilePath + ".tmp");

    try (BufferedReader br = new BufferedReader(new FileReader(originalFile));
         PrintWriter pw = new PrintWriter(new FileWriter(tempFile))) {

        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 4 && parts[0].equals(voter.getVoterId())) {
                // Update the hasVoted status to true
                parts[3] = "true";
                pw.println(String.join(",", parts));
            } else {
                pw.println(line); // Write unchanged lines
            }
        }

        // Close the resources before file manipulation
        br.close();
        pw.close();

        // Replace the original file with the updated temp file
        if (originalFile.delete() && tempFile.renameTo(originalFile)) {
            updateSuccess = true; // File update successful
        } else {
            System.err.println("Failed to replace the original voter file.");
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    return updateSuccess;
}
}

class ExecuteVote {
    public static void main(String[] args) {
        VoterLoad voterLoad = new VoterLoad();
        voterLoad.loadVoters(); // Load the voters from the file
        new VoterValidationUI(voterLoad); // Use the VoterLoad constructor
    }
}