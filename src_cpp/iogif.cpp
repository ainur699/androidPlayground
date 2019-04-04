//
// Created by Ainur on 04.04.2019.
//

#include "iogif.hpp"
#include "giflib/gif_lib.h"


struct MemoryStruct
{
    uchar *data;
    size_t size;
};

int read_func(GifFileType *gif, GifByteType *buf, int size)
{
    MemoryStruct *mem = reinterpret_cast<MemoryStruct *>(gif->UserData);

    if (mem->size) {
        int copy_size = std::min((size_t)size, mem->size);
        std::memcpy(buf, mem->data, copy_size);
        mem->data += copy_size;
        size -= copy_size;

        return copy_size;
    }

    return 0;
}

int write_func(GifFileType *gif, const GifByteType *buf, int size)
{
    MemoryStruct *mem = reinterpret_cast<MemoryStruct *>(gif->UserData);

    uchar *ptr = (uchar*)std::realloc(mem->data, mem->size + size);
    if (!ptr) return 0;

    mem->data = ptr;
    std::memcpy(mem->data + mem->size, buf, size);
    mem->size += size;

    return size;
}

std::vector<cv::Mat> gif_read(const std::vector<uchar> &raw_data)
{
    int error;
    std::vector<cv::Mat> ret;

    MemoryStruct user_data{ (uchar*)raw_data.data(), raw_data.size()};

    GifFileType* gif = DGifOpen(&user_data, read_func, &error);
    if (gif == NULL) return ret;


    int slurpReturn = DGifSlurp(gif);
    if (slurpReturn != GIF_OK) return ret;

    cv::Mat dst(gif->SHeight, gif->SWidth, CV_8UC3);

    for (int i = 0; i < gif->ImageCount; i++)
    {
        SavedImage &im = gif->SavedImages[i];
        int Bottom = im.ImageDesc.Top + im.ImageDesc.Height;
        int Right = im.ImageDesc.Left + im.ImageDesc.Width;

#if 0// extecsions
        for (size_t j = 0; j < im.ExtensionBlockCount; j++) {
			std::cout << "func " << im.ExtensionBlocks[j].Function << std::endl;
			for (size_t k = 0; k < im.ExtensionBlocks[j].ByteCount; k++) {
				std::cout << (int)im.ExtensionBlocks[j].Bytes[k] << " ";
			}
			std::cout << std::endl;
		}
		std::cout << std::endl;
#endif
        cv::Mat frame(im.ImageDesc.Height, im.ImageDesc.Width, CV_8UC1, im.RasterBits);

        for (int row = im.ImageDesc.Top; row < Bottom; row++) {
            uchar * p_frame = frame.ptr(row - im.ImageDesc.Top);
            cv::Vec3b *p_dst = dst.ptr<cv::Vec3b>(row);

            for (int col = im.ImageDesc.Left; col < Right; col++) {
                std::memcpy(&p_dst[col], &gif->SColorMap->Colors[p_frame[col - im.ImageDesc.Left]], 3);
            }
        }

        ret.push_back(dst.clone());
    }

    return ret;
}

bool AddLoop(GifFileType *gf)
{
    int loop_count;
    loop_count = 0;
    {
        char nsle[12] = "NETSCAPE2.0";
        char subblock[3];
        if (EGifPutExtensionLeader(gf, APPLICATION_EXT_FUNC_CODE) == GIF_ERROR) return false;

        subblock[0] = 1;
        subblock[2] = loop_count % 256;
        subblock[1] = loop_count / 256;

        if (EGifPutExtensionBlock(gf, 3, subblock) == GIF_ERROR) return false;
        if (EGifPutExtensionTrailer(gf) == GIF_ERROR) return false;
    }
    return true;
}


void gif_write(std::vector<uchar> &raw_data, const std::vector<cv::Mat> &gif)
{
    /* Open stdout for the output file: */
    int error;
    MemoryStruct mem{ (uchar*)std::malloc(1), 0 };
    GifFileType *GifFile = EGifOpen(&mem, write_func, &error);
    if (GifFile == NULL) return;

    int Width = gif[0].cols;
    int Height = gif[0].rows;

    int ExpNumOfColors = 8;
    int ColorMapSize = 1 << ExpNumOfColors;
    ColorMapObject *OutputColorMap = GifMakeMapObject(ColorMapSize, NULL);
    GifByteType *OutputBuffer = (GifByteType *)malloc(Width * Height * sizeof(GifByteType));
    if (OutputColorMap == NULL || OutputBuffer == NULL) return;

    for (int i = 0; i < gif.size(); i++)
    {
        std::vector<cv::Mat> channels;
        cv::split(gif[i], channels);
        GifByteType *RedBuffer = channels[0].data;
        GifByteType *GreenBuffer = channels[1].data;
        GifByteType *BlueBuffer = channels[2].data;

        if (i == 0) {
            if (GifQuantizeBuffer(Width, Height, &ColorMapSize, RedBuffer, GreenBuffer, BlueBuffer, OutputBuffer, OutputColorMap->Colors) == GIF_ERROR) return;
            if (EGifPutScreenDesc(GifFile, Width, Height, ExpNumOfColors, 0, OutputColorMap) == GIF_ERROR) return;
            if (!AddLoop(GifFile)) return;
        }
        else
        {
            unsigned int npix = Width * Height;
            for (int j = 0; j < npix; j++)
            {
                int minIndex = 0, minDist = 3 * 256 * 256;
                GifColorType *c = OutputColorMap->Colors;

                /* Find closest color in first color map to this color. */
                for (int k = 0; k < OutputColorMap->ColorCount; k++) {
                    int dr = (int(c[k].Red) - RedBuffer[j]);
                    int dg = (int(c[k].Green) - GreenBuffer[j]);
                    int db = (int(c[k].Blue) - BlueBuffer[j]);

                    int dist = dr * dr + dg * dg + db * db;

                    if (minDist > dist) {
                        minDist = dist;
                        minIndex = k;
                    }
                }
                OutputBuffer[j] = minIndex;
            }
        }

        static unsigned char ExtStr[4] = { 0x04, 0x00, 0x00, 0xff };
        ExtStr[0] = (false) ? 0x06 : 0x04;
        ExtStr[1] = 5 % 256;
        ExtStr[2] = 5 / 256;
        EGifPutExtension(GifFile, GRAPHICS_EXT_FUNC_CODE, 4, ExtStr);
        if (EGifPutImageDesc(GifFile, 0, 0, Width, Height, false, NULL) == GIF_ERROR) return;

        GifByteType *Ptr = OutputBuffer;
        for (int i = 0; i < Height; i++) {
            if (EGifPutLine(GifFile, Ptr, Width) == GIF_ERROR) return;
            Ptr += Width;
        }
    }

    if (EGifCloseFile(GifFile, &error) == GIF_ERROR) return;

    raw_data.assign(mem.data, mem.data + mem.size);
    free(mem.data);
}