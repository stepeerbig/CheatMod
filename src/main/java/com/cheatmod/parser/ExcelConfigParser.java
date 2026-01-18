package com.cheatmod.parser;

import com.cheatmod.config.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ExcelConfigParser {
    
    public static void main(String[] args) {
        System.out.println("=== Запуск парсера Excel ===");
        System.out.println("Текущая директория: " + System.getProperty("user.dir"));
        
        try {
            // Пробуем разные пути к файлу
            String[] possiblePaths = {
                "config_items.xlsx",
                "./config_items.xlsx",
                System.getProperty("user.dir") + "/config_items.xlsx",
                "E:/CheatMod/config_items.xlsx"  // Укажите ваш реальный путь
            };
            
            File excelFile = null;
            for (String path : possiblePaths) {
                File testFile = new File(path);
                System.out.println("Проверяем путь: " + testFile.getAbsolutePath());
                if (testFile.exists() && testFile.isFile()) {
                    excelFile = testFile;
                    System.out.println("✓ Файл найден!");
                    break;
                }
            }
            
            if (excelFile == null) {
                System.err.println("✗ ОШИБКА: Файл config_items.xlsx не найден!");
                System.err.println("Поместите файл в одну из этих папок:");
                for (String path : possiblePaths) {
                    System.err.println("  - " + new File(path).getAbsolutePath());
                }
                
                // Показываем содержимое текущей папки
                System.err.println("\nСодержимое текущей папки:");
                File currentDir = new File(".");
                String[] files = currentDir.list();
                if (files != null) {
                    for (String file : files) {
                        System.err.println("  - " + file);
                    }
                }
                return;
            }
            
            System.out.println("\nФайл найден: " + excelFile.getAbsolutePath());
            System.out.println("Размер файла: " + excelFile.length() + " байт");
            
            // Парсим
            ConfigContainer config = parseExcelFile(excelFile);
            
            if (config == null || config.getItems() == null) {
                System.err.println("✗ ОШИБКА: Не удалось распарсить файл");
                return;
            }
            
            System.out.println("\nУспешно распаршено " + config.getItems().size() + " предметов:");
            for (ItemConfig item : config.getItems()) {
                System.out.println("  ✓ " + item.getDisplayName() + 
                                 " [" + item.getMinecraftId() + "] " + 
                                 item.getMinPrice() + "-" + item.getMaxPrice() + " coins");
            }
            
            // Сохраняем
            saveConfig(config, "market.json");
            saveConfig(config, "auction.json");
            
            System.out.println("\n=== Парсинг завершен успешно! ===");
            
        } catch (Exception e) {
            System.err.println("✗ КРИТИЧЕСКАЯ ОШИБКА:");
            e.printStackTrace();
        }
    }
    
    private static ConfigContainer parseExcelFile(File file) throws IOException {
        System.out.println("\nНачинаем парсинг Excel файла...");
        ConfigContainer container = new ConfigContainer();
        
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            System.out.println("Количество листов: " + workbook.getNumberOfSheets());
            
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                System.err.println("✗ Лист 0 не найден в файле!");
                return container;
            }
            
            System.out.println("Имя листа: '" + sheet.getSheetName() + "'");
            System.out.println("Всего строк: " + (sheet.getLastRowNum() + 1));
            
            // Читаем заголовок
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                System.out.println("Заголовки столбцов:");
                for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                    Cell cell = headerRow.getCell(i);
                    System.out.println("  " + i + ": '" + getCellValue(cell) + "'");
                }
            }
            
            int processed = 0;
            int skipped = 0;
            
            // Начинаем с 1 строки (после заголовка)
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    skipped++;
                    continue;
                }
                
                // Пропускаем полностью пустые строки
                boolean isEmpty = true;
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    if (row.getCell(i) != null) {
                        String val = getCellValue(row.getCell(i));
                        if (val != null && !val.trim().isEmpty()) {
                            isEmpty = false;
                            break;
                        }
                    }
                }
                
                if (isEmpty) {
                    skipped++;
                    continue;
                }
                
                ItemConfig item = parseRow(row, rowNum);
                if (item != null) {
                    container.addItem(item);
                    processed++;
                } else {
                    skipped++;
                }
            }
            
            System.out.println("\nИтоги парсинга:");
            System.out.println("  Обработано: " + processed + " строк");
            System.out.println("  Пропущено: " + skipped + " строк");
            
        } catch (Exception e) {
            System.err.println("✗ Ошибка при парсинге Excel файла:");
            e.printStackTrace();
            throw e;
        }
        
        return container;
    }
    
    private static ItemConfig parseRow(Row row, int rowNum) {
        try {
            // Проверяем основные ячейки
            Cell nameCell = row.getCell(0);
            Cell idCell = row.getCell(1);
            Cell minCell = row.getCell(2);
            Cell maxCell = row.getCell(3);
            Cell enchantCell = row.getCell(4);
            
            // Читаем значения
            String name = getCellValue(nameCell);
            String id = getCellValue(idCell);
            
            // Проверяем обязательные поля
            if (name == null || name.trim().isEmpty()) {
                System.out.println("Строка " + (rowNum+1) + ": пропущено название");
                return null;
            }
            
            if (id == null || id.trim().isEmpty()) {
                System.out.println("Строка " + (rowNum+1) + ": пропущен ID");
                return null;
            }
            
            // Читаем цены
            int minPrice = 10;
            int maxPrice = 30;
            
            try {
                if (minCell != null) {
                    double minVal = getNumericValue(minCell, 10);
                    minPrice = (int) minVal;
                }
                
                if (maxCell != null) {
                    double maxVal = getNumericValue(maxCell, 30);
                    maxPrice = (int) maxVal;
                }
            } catch (Exception e) {
                System.out.println("Строка " + (rowNum+1) + ": ошибка чтения цен, используем значения по умолчанию");
            }
            
            // Исправляем, если min > max
            if (minPrice > maxPrice) {
                System.out.println("Строка " + (rowNum+1) + ": min > max, меняем местами");
                int temp = minPrice;
                minPrice = maxPrice;
                maxPrice = temp;
            }
            
            // Создаём предмет
            ItemConfig item = new ItemConfig(name.trim(), id.trim(), minPrice, maxPrice);
            
            // Обрабатываем зачарования, если есть
            if (enchantCell != null) {
                String enchantText = getCellValue(enchantCell);
                if (enchantText != null && !enchantText.trim().isEmpty()) {
                    processEnchantments(item, enchantText.trim());
                }
            }
            
            return item;
            
        } catch (Exception e) {
            System.err.println("✗ Ошибка в строке " + (rowNum+1) + ": " + e.getMessage());
            return null;
        }
    }
    
    private static void processEnchantments(ItemConfig item, String text) {
        if (text == null || text.isEmpty()) return;
        
        System.out.println("  Обработка зачарований: '" + text + "'");
        
        // Убираем русскую букву "а" в начале (возможно опечатка)
        if (text.startsWith("а") || text.startsWith("а ")) {
            text = text.substring(text.startsWith("а ") ? 2 : 1).trim();
        }
        
        // Убираем лишние пробелы
        text = text.replaceAll("\\s+", " ");
        
        // Обработка кастомных эффектов с процентами
        if (text.contains("%")) {
            item.setCustomEffects(text);
            System.out.println("    Установлен кастомный эффект: " + text);
        }
        
        // Приводим к нижнему регистру для поиска
        String lowerText = text.toLowerCase();
        
        // Обработка стандартных зачарований
        if (lowerText.contains("эффективность x")) {
            item.addEnchantment(new EnchantmentConfig("efficiency", 10, 1.0));
            System.out.println("    Добавлено зачарование: efficiency 10");
        }
        if (lowerText.contains("прочность v") || lowerText.contains("прочность iii") || 
            lowerText.contains("нерушим") || lowerText.contains("нерушимая")) {
            item.addEnchantment(new EnchantmentConfig("unbreaking", 5, 1.0));
            System.out.println("    Добавлено зачарование: unbreaking 5");
        }
        if (lowerText.contains("удача iii")) {
            item.addEnchantment(new EnchantmentConfig("fortune", 3, 1.0));
            System.out.println("    Добавлено зачарование: fortune 3");
        }
        if (lowerText.contains("добыча v")) {
            item.addEnchantment(new EnchantmentConfig("looting", 5, 1.0));
            System.out.println("    Добавлено зачарование: looting 5");
        }
        if (lowerText.contains("разящий клинок iii")) {
            item.addEnchantment(new EnchantmentConfig("sharpness", 3, 1.0));
            System.out.println("    Добавлено зачарование: sharpness 3");
        }
        
        // Обработка специальных способностей
        if (lowerText.contains("ломает территорию") || 
            lowerText.contains("имеет свойство") || 
            lowerText.contains("мульти-инструмент") ||
            lowerText.contains("мульти-ниструмент")) {
            item.setSpecialAbility(text);
            System.out.println("    Установлена специальная способность: " + text);
        }
    }
    
    private static String getCellValue(Cell cell) {
        if (cell == null) return null;
        
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        double value = cell.getNumericCellValue();
                        if (value == Math.floor(value) && !Double.isInfinite(value)) {
                            return String.valueOf((int) value);
                        } else {
                            return String.valueOf(value);
                        }
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return cell.getStringCellValue();
                    } catch (Exception e) {
                        try {
                            return String.valueOf((int) cell.getNumericCellValue());
                        } catch (Exception ex) {
                            return cell.getCellFormula();
                        }
                    }
                case BLANK:
                    return "";
                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Ошибка чтения ячейки: " + e.getMessage());
            return null;
        }
    }
    
    private static double getNumericValue(Cell cell, double defaultValue) {
        if (cell == null) return defaultValue;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String str = cell.getStringCellValue().trim();
                if (str.isEmpty()) return defaultValue;
                return Double.parseDouble(str);
            } else if (cell.getCellType() == CellType.FORMULA) {
                try {
                    return cell.getNumericCellValue();
                } catch (Exception e) {
                    return defaultValue;
                }
            }
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    private static void saveConfig(ConfigContainer config, String filename) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(config);
        
        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(json);
            System.out.println("✓ Конфиг сохранен: " + file.getAbsolutePath());
        }
    }
}