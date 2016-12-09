/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */

package data;

/**
 * An exception thrown when at attempt is made to treat as a Double an object that is not one.
 *
 * @author Jacob Rachiele
 * Date: Dec 09 2016
 *
 */
public class NotADoubleException extends RuntimeException {

  /**
   * Create a new exception with the default message.
   */
  public NotADoubleException() {
    this("An attempt was made to treat a non-Double object as a Double.");
  }

  /**
   * Create a new exception with the given message.
   * @param message the message to use to inform the client why the exception was created.
   */
  public NotADoubleException(String message) {
    super(message);
  }
}
