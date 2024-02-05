/**
  ******************************************************************************
  * @file           : main.c
  * @brief          : Main program body
  ******************************************************************************
  * @attention
  *
  * Copyright (c) 2024 STMicroelectronics.
  * All rights reserved.
  *
  * This software is licensed under terms that can be found in the LICENSE file
  * in the root directory of this software component.
  * If no LICENSE file comes with this software, it is provided AS-IS.
  *
  ******************************************************************************
  */

/* Includes ------------------------------------------------------------------*/
#include "main.h"
#include <string.h>
/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */

/* USER CODE END Includes */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
/* USER CODE BEGIN PD */

/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/
UART_HandleTypeDef huart1;

/* USER CODE BEGIN PV */

#define DATABLOCKSIZEBYTES 32
#define MX (right_shift >> 5 ^ left_shift << 2) + (left_shift >> 3 ^ right_shift << 4) ^ (sum ^ left_shift) + (key[p & 3 ^ e] ^ right_shift)
uint8_t uartDataRx[DATABLOCKSIZEBYTES];  
uint8_t uartDataTx[DATABLOCKSIZEBYTES];
uint8_t numBlocks[2];
uint8_t allBytes = 0;
/* USER CODE END PV */
uint32_t encryption_key[4] = {0x12345678, 0xabcdef01, 0x87654321, 0xfedcba98};
/* Private function prototypes -----------------------------------------------*/
void SystemClock_Config(void);
static void MX_GPIO_Init(void);
static void MX_USART1_UART_Init(void);
uint8_t calculateChecksum(uint8_t* data, uint16_t length);
HAL_StatusTypeDef UART_Send(uint8_t* data, uint16_t byte_count);
HAL_StatusTypeDef UART_Receive(uint8_t* data, uint16_t byte_count);
void processReceivedData(uint8_t* ReceivedData);
static void xxtea_encrypt(uint32_t* data, uint32_t* key, size_t* len);
static void xxtea_decrypt(uint32_t* data, uint32_t* key, size_t* len);
/* USER CODE BEGIN PFP */

/* USER CODE END PFP */

/* Private user code ---------------------------------------------------------*/
/* USER CODE BEGIN 0 */

uint8_t calculateChecksum(uint8_t* data, uint16_t length){
    uint8_t bcc = 0;
    for(int i = 0; i < length; ++i){
        bcc ^= data[i];
    }
    return bcc;
}

HAL_StatusTypeDef UART_Send(uint8_t* data, uint16_t byte_count) { 
    return HAL_UART_Transmit(&huart1, data, byte_count, HAL_MAX_DELAY);
}

HAL_StatusTypeDef UART_Receive(uint8_t* data, uint16_t byte_count) {   
    return HAL_UART_Receive(&huart1, data, byte_count, HAL_MAX_DELAY);
}

void processReceivedData(uint8_t* ReceivedData)
{
    uint8_t checksum = calculateChecksum(ReceivedData, DATABLOCKSIZEBYTES);
		//I should add check
}
static void xxtea_encrypt(uint32_t* data, uint32_t* key, size_t* len) {

    uint32_t word_number = (uint32_t)(*len);
    uint32_t cycle_round = 6 + 52 / word_number;

    uint32_t  sum = 0, e = 0, p = 0;
    uint32_t right_shift = data[word_number - 1];
    uint32_t left_shift = data [0];

    if (word_number < 1) return;

    for (int i = 0; i < cycle_round; i++) {

        sum += 0x9E3779B9; // Use the hexadecimal constant directly
        e = sum >> 2 & 3;

        for (p = 0; p < word_number - 1; p++) {

            left_shift = data[p + 1];
            right_shift = data[p] += MX;
        }

        left_shift = data[0];
        right_shift = data[word_number - 1] += MX;
    }
}
static void xxtea_decrypt(uint32_t* data, uint32_t* key, size_t* len) {
    // Some initialization
    uint32_t word_number = (uint32_t)(*len);
    uint32_t q = 6 + 52 / word_number;
    uint32_t sum = q * 0x9E3779B9;
    uint32_t right_shift = data[word_number - 1];
    uint32_t left_shift = data[0];
    uint32_t p;
	

    // Loop for multiple rounds
    while (sum != 0) {
        uint32_t e = sum >> 2 & 3;
        for (p = word_number - 1; p > 0; p--) {
            right_shift = data[p - 1];
            left_shift = data[p] -= MX;
        }
        right_shift = data[word_number - 1];
        left_shift = data[0] -= MX;
        sum -= 0x9E3779B9;

    }
}



/* USER CODE END 0 */

/**
  * @brief  The application entry point.
  * @retval int
  */
int main(void)
{
    /* USER CODE BEGIN 1 */
    /* USER CODE END 1 */

    /* MCU Configuration--------------------------------------------------------*/

    /* Reset of all peripherals, Initializes the Flash interface and the Systick. */
    HAL_Init();

    /* USER CODE BEGIN Init */

    /* USER CODE END Init */

    /* Configure the system clock */
    SystemClock_Config();

    /* USER CODE BEGIN SysInit */

    /* USER CODE END SysInit */

    /* Initialize all configured peripherals */
    MX_GPIO_Init();
    MX_USART1_UART_Init();
    /* USER CODE BEGIN 2 */
    /* USER CODE END 2 */
    /* Infinite loop */
    /* USER CODE BEGIN WHILE */
    while (1)
    {
        /* USER CODE END WHILE */
					
        /* USER CODE BEGIN 3 */
			receiveNumBlocks:
				UART_Receive(numBlocks, 1);
				switch(numBlocks[1]){
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
					goto receiveNumBlocks;
				}
				uint8_t okSend[] = "GreatJob!";
				UART_Send(okSend, sizeof(okSend));
				
				uint8_t generalDataReceived[allBytes];
				int counter = 0;
			while (counter < allBytes) {
    if (UART_Receive(uartDataRx, DATABLOCKSIZEBYTES) == HAL_OK) {
				size_t data_length_words = sizeof(uartDataRx) / sizeof(uint32_t);
			  if(numBlocks[0] == (uint8_t)'l'){
				xxtea_encrypt((uint32_t*)uartDataRx, encryption_key, &data_length_words);
				}
				else if(numBlocks[0] == (uint8_t)'u'){
					xxtea_decrypt((uint32_t*)uartDataRx, encryption_key, &data_length_words);
				}
        for (int i = counter; (i < DATABLOCKSIZEBYTES + counter) && i < allBytes; ++i) {
            generalDataReceived[i] = uartDataRx[i - counter];
        }
				
        counter += DATABLOCKSIZEBYTES;
    } 
}
		counter = 0;
	while (counter < allBytes){
		for(int i = counter; (i < DATABLOCKSIZEBYTES + counter) && i < allBytes; ++i){
			uartDataTx[i - counter] =  generalDataReceived[i];
		}
		if(UART_Send(uartDataTx, DATABLOCKSIZEBYTES) == HAL_OK){
		}
		counter += DATABLOCKSIZEBYTES;
	}
				
    /* USER CODE END 3 */
}
		}
/**
  * @brief System Clock Configuration
  * @retval None
  */
void SystemClock_Config(void)
{
    RCC_OscInitTypeDef RCC_OscInitStruct = {0};
    RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};
    RCC_PeriphCLKInitTypeDef PeriphClkInit = {0};

    /** Initializes the RCC Oscillators according to the specified parameters
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

    /** Initializes the CPU, AHB and APB buses clocks
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

/**
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

/**
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

/**
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
/**
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