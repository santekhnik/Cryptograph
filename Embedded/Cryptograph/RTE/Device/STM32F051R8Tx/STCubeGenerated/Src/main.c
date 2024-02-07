#include "main.h"
#include <string.h>
#include <stdint.h>
#include "stm32f0xx_hal.h"

UART_HandleTypeDef huart1;
#define MX (right_shift >> 5 ^ left_shift << 2) + (left_shift >> 3 ^ right_shift << 4) ^ (sum ^ left_shift) + (key[p & 3 ^ e] ^ right_shift)
#define DATABLOCKSIZEBYTES 32
void SystemClock_Config(void);
static void MX_GPIO_Init(void);
static void MX_USART1_UART_Init(void);
uint8_t calculateChecksum(uint8_t* data, uint8_t length);
void processReceivedData(uint8_t* ReceivedData);
HAL_StatusTypeDef UART_Send(uint8_t* data, uint16_t byte_count);
HAL_StatusTypeDef UART_Receive(uint8_t* data, uint16_t byte_count);
static void xxtea_encrypt(uint8_t* data, uint8_t* key, size_t* len);
static void xxtea_decrypt(uint8_t* data, uint8_t* key, size_t* len);
void SystemClock_Config(void);
static void MX_GPIO_Init(void);
static void MX_USART1_UART_Init(void);

uint8_t calculateChecksum(uint8_t* data, uint8_t length){
    uint8_t bcc = 0;
    for(int i = 0; i < length; ++i){
        bcc ^= data[i];
    }
    return bcc;
}
HAL_StatusTypeDef UART_Send(uint8_t* data, uint16_t byte_count)
{
    return HAL_UART_Transmit(&huart1, data, byte_count, HAL_MAX_DELAY);
}
HAL_StatusTypeDef UART_Receive(uint8_t* data, uint16_t byte_count)
{
    return HAL_UART_Receive(&huart1, data, byte_count, HAL_MAX_DELAY);
}

static void xxtea_encrypt(uint8_t* data, uint8_t* key, size_t* len) {

    uint8_t word_number = (uint8_t)(*len);
    uint8_t cycle_round = 6 + 52 / word_number;

    uint8_t sum = 0, e = 0, p = 0;
    uint8_t right_shift = data[word_number - 1];
    uint8_t left_shift = data[0];

    if (word_number < 1) return;

    for (int i = 0; i < cycle_round; i++) {
        sum += 0x11;
        e = sum >> 2 & 3;

        for (p = 0; p < word_number - 1; p++) {
            left_shift = data[p + 1];
            right_shift = data[p] += MX;
        }

        left_shift = data[0];
        right_shift = data[word_number - 1] += MX;
    }
}
static void xxtea_decrypt(uint8_t* data, uint8_t* key, size_t* len) {
    uint8_t word_number = (uint8_t)(*len);
    uint8_t q = 6 + 52 / word_number;
    uint8_t sum = q * 0x11;
    uint8_t right_shift = data[word_number - 1];
    uint8_t left_shift = data[0];
    uint8_t p;

    while (sum != 0) {
        uint8_t e = sum >> 2 & 3;
        for (p = word_number - 1; p > 0; p--) {
            right_shift = data[p - 1];
            left_shift = data[p] -= MX;
        }
        right_shift = data[word_number - 1];
        left_shift = data[0] -= MX;
        sum -= 0x11;
    }
}


int main(void)
{
	HAL_Init(); 
	SystemClock_Config(); 
	MX_GPIO_Init(); 
	MX_USART1_UART_Init(); 
	//USER CODE
	uint8_t blocksAndOption[2];
	uint8_t allBytes = 0;
	uint8_t uartDataRx[DATABLOCKSIZEBYTES];
	uint8_t uartDataTx[DATABLOCKSIZEBYTES];
	uint8_t key[4] = { 0x92, 0x93, 0x92, 0x92 };
	uint8_t okSend[2] = "Ok";
	uint8_t badSend[2] = "No";
	uint8_t answer1[11] = " Encrypted ";
	uint8_t answer2[11] = " Decrypted ";
	uint8_t answer3[12] = " Sending... ";
	size_t len = sizeof(uartDataRx) / sizeof(uint8_t);
	
	while(1) {
		receiveBlocksAndOption:
			UART_Receive(blocksAndOption, 2);
			switch( blocksAndOption[1]){
				case (uint8_t)'a':
						allBytes = 32;
				break;
				case (uint8_t)'b':
						allBytes = 64;
				break;
				case (uint8_t)'c':
						allBytes = 96;
				break;
				case (uint8_t)'d':
						allBytes = 128;
				break;
				default: 
					UART_Send(badSend, sizeof(badSend));
					memset(blocksAndOption, 0, 2);
					goto receiveBlocksAndOption;
				
				}
		uint8_t generalDataReceived[allBytes];
		UART_Send(okSend, sizeof(okSend));
		int counter = 0;
		while (counter < allBytes) {
			UART_Receive(uartDataRx, DATABLOCKSIZEBYTES);
			if (blocksAndOption[0] == 'l') {
				xxtea_encrypt(uartDataRx, key, &len);
				UART_Send(answer1, sizeof(answer1));
			}
			else if (blocksAndOption[0] == 'u') {
				xxtea_decrypt(uartDataRx, key, &len);
				UART_Send(answer2, sizeof(answer2));
			}
			for (int i = counter; i < DATABLOCKSIZEBYTES + counter && i < allBytes; ++i) {
				generalDataReceived[i] = uartDataRx[i - counter];
		}
			memset(uartDataRx, 0, DATABLOCKSIZEBYTES);
			counter += DATABLOCKSIZEBYTES;
		}
		UART_Send(answer3, sizeof(answer3));
		counter = 0;
		while (counter < allBytes){
			memset(uartDataTx, 0, DATABLOCKSIZEBYTES);
			for(int i = counter; i < DATABLOCKSIZEBYTES + counter && i < allBytes; ++i){
				uartDataTx[i - counter] =  generalDataReceived[i];
		}
			UART_Send(uartDataTx, DATABLOCKSIZEBYTES);
			counter += DATABLOCKSIZEBYTES;
		}
	}
}

    
    
void SystemClock_Config(void)
{
    RCC_OscInitTypeDef RCC_OscInitStruct = {0};
    RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};
    RCC_PeriphCLKInitTypeDef PeriphClkInit = {0};

/* Initializes the RCC Oscillators according to the specified parameters
  * in the RCC_OscInitTypeDef structure.
  */
    RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSI;
    RCC_OscInitStruct.HSIState = RCC_HSI_ON;
    RCC_OscInitStruct.HSICalibrationValue = RCC_HSICALIBRATION_DEFAULT;
    RCC_OscInitStruct.PLL.PLLState = RCC_PLL_NONE;
    if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK)
    {
        Error_Handler();
    }

    /* Initializes the CPU, AHB and APB buses clocks
  */
    RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK | RCC_CLOCKTYPE_SYSCLK |
                                   RCC_CLOCKTYPE_PCLK1;
    RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_HSI;
    RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
    RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV1;

    if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_0) != HAL_OK)
    {
        Error_Handler();
    }
    PeriphClkInit.PeriphClockSelection = RCC_PERIPHCLK_USART1;
    PeriphClkInit.Usart1ClockSelection = RCC_USART1CLKSOURCE_PCLK1;
    if (HAL_RCCEx_PeriphCLKConfig(&PeriphClkInit) != HAL_OK)
    {
        Error_Handler();
    }
}

/*
  * @brief USART1 Initialization Function
  * @param None
  * @retval None
  */
static void MX_USART1_UART_Init(void)
{

    /* USER CODE BEGIN USART1_Init 0 */

    /* USER CODE END USART1_Init 0 */

    /* USER CODE BEGIN USART1_Init 1 */

    /* USER CODE END USART1_Init 1 */
    huart1.Instance = USART1;
    huart1.Init.BaudRate = 38400;
    huart1.Init.WordLength = UART_WORDLENGTH_8B;
    huart1.Init.StopBits = UART_STOPBITS_1;
    huart1.Init.Parity = UART_PARITY_NONE;
    huart1.Init.Mode = UART_MODE_TX_RX;
    huart1.Init.HwFlowCtl = UART_HWCONTROL_NONE;
    huart1.Init.OverSampling = UART_OVERSAMPLING_16;
    huart1.Init.OneBitSampling = UART_ONE_BIT_SAMPLE_DISABLE;
    huart1.AdvancedInit.AdvFeatureInit = UART_ADVFEATURE_NO_INIT;
    if (HAL_UART_Init(&huart1) != HAL_OK)
    {
        Error_Handler();
    }
    /* USER CODE BEGIN USART1_Init 2 */

    /* USER CODE END USART1_Init 2 */
}

/*
  * @brief GPIO Initialization Function
  * @param None
  * @retval None
  */
static void MX_GPIO_Init(void)
{
    GPIO_InitTypeDef GPIO_InitStruct = {0};
    /* USER CODE BEGIN MX_GPIO_Init_1 */
    /* USER CODE END MX_GPIO_Init_1 */

    /* GPIO Ports Clock Enable */
    __HAL_RCC_GPIOC_CLK_ENABLE();
    __HAL_RCC_GPIOA_CLK_ENABLE();

    /*Configure GPIO pin Output Level */
    HAL_GPIO_WritePin(GPIOC, GPIO_PIN_8, GPIO_PIN_RESET);

    /*Configure GPIO pin : PC8 */
    GPIO_InitStruct.Pin = GPIO_PIN_8;
    GPIO_InitStruct.Mode = GPIO_MODE_OUTPUT_PP;
    GPIO_InitStruct.Pull = GPIO_NOPULL;
    GPIO_InitStruct.Speed = GPIO_SPEED_FREQ_LOW;
    HAL_GPIO_Init(GPIOC, &GPIO_InitStruct);

    /* USER CODE BEGIN MX_GPIO_Init_2 */
    /* USER CODE END MX_GPIO_Init_2 */
}

/* USER CODE BEGIN 4 */

/* USER CODE END 4 */

/*
  * @brief  This function is executed in case of error occurrence.
  * @retval None
  */
void Error_Handler(void)
{
    /* USER CODE BEGIN Error_Handler_Debug */
    /* User can add his own implementation to report the HAL error return state */
    __disable_irq();
    while (1)
    {
    }
    /* USER CODE END Error_Handler_Debug */
}

#ifdef USE_FULL_ASSERT
/
  * @brief  Reports the name of the source file and the source line number
  *         where the assert_param error has occurred.
  * @param  file: pointer to the source file name
  * @param  line: assert_param error line source number
  * @retval None
  */
void assert_failed(uint8_t *file, uint32_t line)
{
    /* USER CODE BEGIN 6 */
    /* User can add his own implementation to report the file name and line number,
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */
    /* USER CODE END 6 */
}
#endif /* USE_FULL_ASSERT */