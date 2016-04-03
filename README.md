# MaekawaProtocol
Submission by 
Apoorv Singh (axk132231)
Ram Hariesh Coimbatore Annadorai (rxc142330)
Sarat Chandra Varanasi (sxv153030)
To Run the project
-------------------
1. cd into the folder that contains launcher.sh
2. ./launcher.sh <path_to_config_file> <net_id>

To Test the correctness of the project
--------------------------------------
From the same folder

1. cp foo* correctness 
2. cd into correctness folder
3. javac Vector.java
4. java Vector <Number_of_processes> <Number_of_Cricital_section_requests_per_process>
5. if the output says "NO! PROTOCOL DOES NOT WORK!" then the mutual exclusion is violated/
6. if the output says "NO CONCURRENT CRITICAL SECTIONS!" ten the protocol works.
