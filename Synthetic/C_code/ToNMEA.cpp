#include <iostream>
#include <fstream> //for read and write file
#include <string>
#include <vector>
#include <cmath>
#include <iomanip>

// + = North, East
// - = West, South

std::string CalculateCheckSum(std::string myString){
	int temp_checkSum = 0;
	std::vector<std::string> checkS;
	std::string checkSum = "";
	for (int i = 0; i < myString.length(); i++)
	{
		temp_checkSum ^= myString[i];
	}
	while (temp_checkSum != 0)
	{
		int remainder = temp_checkSum % 16;
		if (remainder < 10)
			checkS.push_back(std::to_string(remainder));
		else if (remainder == 10)
			checkS.push_back("A");
		else if (remainder == 11)
			checkS.push_back("B");
		else if (remainder == 12)
			checkS.push_back("C");
		else if (remainder == 13)
			checkS.push_back("D");
		else if (remainder == 14)
			checkS.push_back("E");
		else if (remainder == 15)
			checkS.push_back("F");
		temp_checkSum = temp_checkSum / 16;
	}
	std::vector<std::string>::reverse_iterator rite = checkS.rbegin();
	for (rite; rite != checkS.rend(); rite++)
	{
		checkSum += *rite;
	}
	return checkSum;
}

std::string DecimalToDegree(double decimal, bool flag){
	std::string a = std::to_string(static_cast<int>(std::floor(decimal)));
	if (flag == true)
	{
		if (std::floor(decimal) < 100)
			a = "0" + a;
	}
	std::string b;
	double temp_b = (decimal - std::floor(decimal))*60.0;

	//make b a-3-decimal-point-number:
	temp_b = std::floor((temp_b * std::pow(10.0, 3.0)) + 0.5) / std::pow(10.0,3.0);
	b = std::to_string(temp_b);
	return a+b;
}

int main(int argc, char **argv){
	int timeTaken = 123519;
	std::string remainingVars = "2,10,0.6,791.6,M,-32.8,M,,";
	
	std::vector<double> vectOfNMEA;
	std::ifstream inputFile("../Inputs/NMEA_input.txt");
	std::string str;
	if (inputFile.is_open())
	{
		while (std::getline(inputFile, str))
		{
			vectOfNMEA.push_back(std::stod(str));
		}
		inputFile.close();
	}
	int count = 1;
	std::vector<double>::iterator ite = vectOfNMEA.begin();
	std::string latitude;
	std::string longtitude;
	std::ofstream outputFile;
	for (ite; ite != vectOfNMEA.end(); ite++)
	{
		std::string myString;
		outputFile.open("../Inputs/NMEA.txt", std::ios_base::app);
		switch (count)
		{
			case 1:
			{
				count = 2;
				double temp_latitude = *ite;
				if (temp_latitude >= 0)
				{
					latitude = DecimalToDegree(temp_latitude, false);
					latitude += ",N,";
				}
				else
				{
					latitude = DecimalToDegree(-temp_latitude, false);
					latitude += ",S,";
				}
				break;
			}
			case 2:
			{
				count = 3;
				double temp_longtitude = *ite;
				if (temp_longtitude >= 0)
				{
					longtitude = DecimalToDegree(temp_longtitude, true);
					longtitude += ",E,";
				}
				else
				{
					longtitude = DecimalToDegree(-temp_longtitude, true);
					longtitude += ",W,";
				}
				myString = "GPGGA," + std::to_string(timeTaken++) + "," + latitude + longtitude + remainingVars;
				std::string checkSum = CalculateCheckSum(myString); 
				myString = "$" + myString + "*" + checkSum;
				outputFile << myString << std::endl;
				break;
			}
			case 3:
			{
				count = 1;
				std::string foot;
				std::string meter;
				std::string fathom;
				double temp_depth = *ite;
				foot = std::to_string(temp_depth);
				meter = std::to_string(std::floor(std::pow(10.0, 2.0) * (temp_depth/3.2808) + 0.5) / std::pow(10.0, 2.0));
				fathom = std::to_string(std::floor(std::pow(10.0, 2.0) * (temp_depth/6.0) + 0.5) / std::pow(10.0, 2.0));
				myString = "SDDBT," + foot + ",f," + meter + ",M," + fathom + ",F";
				std::string checkSum = CalculateCheckSum(myString);
				myString = "$" + myString + "*" + checkSum;
				outputFile << myString << std::endl;
				break;
			}
		}
		outputFile.close();
	}
	return 0;
}
