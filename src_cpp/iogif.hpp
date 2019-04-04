//
// Created by Ainur on 04.04.2019.
//

#ifndef ANDROID_IOGIF_HPP
#define ANDROID_IOGIF_HPP

#include <opencv2/core.hpp>


std::vector<cv::Mat> gif_read(const std::vector<uchar> &raw_data);

void gif_write(std::vector<uchar> &raw_data, const std::vector<cv::Mat> &gif);

#endif //ANDROID_IOGIF_HPP
