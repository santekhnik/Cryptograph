Protocol and checksum algorithm   

STM32F051R8 device will encrypt and decrypt files. Also, UART is used fortransferring commands between PC and STM.
Command list:
 All request commands are presented in six-byte numeric format:
| Command | Specific                         | BCC    |  
|---------|----------------------------------|--------|
| 1 byte  |1 byte | 1 byte | 1 byte | 1 byte | 1 byte |          

 In which the first byte specifies the command number, the next 4 are responsible for the type of the sent command, and the sixth byte performs the calculation of the BCC. The device uses the XOR (example below) method to calculate the BCC.
	Example:
Command	Specific	BCC
0x05	0x01	0x00	0x00	0x01	0x03

 1. 0x05 XOR 0x01 = 0x04 - comparing the first and second byte; 
 2. 0x04 XOR 0x00 = 0x04 - comparing the result of the first and second byte and the third byte; 
 3. 0x04 XOR 0x00 = 0x04 - comparing the result of the previous step with the third byte; 
 4. 0x04 XOR 0x01 = 0x03 - result of the BCC.

________________________________________



________________________________________

1.	Is device connected
This command checks the connection between the PC and the STM.

●	PC->STM sends the command 
Command	Specific	BCC
0x01	0x00	0x00	0x00	0x01	0x00

●	STM->PC sends a response
Command	Response.	BCC
0x01	0x00	0x00	0x00	0x00	0x00	0x01	0x00

If STM is connected, proceed to the next step.
Otherwise, it displays the message: "STM is not connected".

________________________________________

2.	Encrypt
This command sends a file from the PC to the STM for encryption and then returns the encrypted file to the PC.
ー file selection
ー reading file by blocks
ー sending blocks to COM port
ー receiving encrypted blocks
ー combining blocks into output file

●	PC->STM sends the file to be encrypted
Command	Specific	BCC
0x02	0x00	0x00	0x00	0x01	0x01

●	STM->PC reads the file encrypts and sends the encrypted file by sending the command
Command	Response.	BCC
0x02	0x00	0x00	0x00	0x00	0x00	0x01	0x01

If the output is an encrypted file, we move on to the next step.
Otherwise, it displays the message: "File could not be encrypted".
________________________________________

________________________________________

3.	Decrypt
This command sends an encrypted file from the PC to the STM for decryption and then returns the decrypted file to the PC. Decryption functionality is similar to encryption.

●	PC->STM sends the file to be decrypted
Command	Specific	BCC
0x03	0x00	0x00	0x00	0x01	0x02

●	STM->PC reads the file decrypts it and sends the decrypted file by sending the command
Command	Response.	BCC
0x03	0x00	0x00	0x00	0x00	0x00	0x01	0x02

If the output is a decrypted file, we move on to the next step.
Otherwise, it displays the message: "File could not be decrypted".

________________________________________

4.	Ask max block size
This command allows you to define the max block size

●	PC->STM sends a command requesting the maximum data size that can be processed
Command	Specific	BCC
0x04	0x00	0x00	0x00	0x01	0x03

●	STM->PC sends a command with the maximum data size
Command	Response.	BCC
0x04	0x00	0x00	0x00	0x00	0x00	0x01	0x03

The output is the maximum data size that can be processed.
Otherwise, it displays a message: "Error".

________________________________________






________________________________________
     Encryption algorithm
